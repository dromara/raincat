/*
 *
 * Copyright 2017-2018 549477611@qq.com(xiaoyu)
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.raincat.core.spi.repository;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.raincat.common.bean.TransactionRecover;
import com.raincat.common.config.TxConfig;
import com.raincat.common.config.TxRedisConfig;
import com.raincat.common.constant.CommonConstant;
import com.raincat.common.enums.CompensationCacheTypeEnum;
import com.raincat.common.enums.CompensationOperationTypeEnum;
import com.raincat.common.exception.TransactionIoException;
import com.raincat.common.exception.TransactionRuntimeException;
import com.raincat.common.holder.LogUtil;
import com.raincat.common.holder.RepositoryPathUtils;
import com.raincat.common.holder.TransactionRecoverUtils;
import com.raincat.common.jedis.JedisClient;
import com.raincat.common.jedis.JedisClientCluster;
import com.raincat.common.jedis.JedisClientSingle;
import com.raincat.common.serializer.ObjectSerializer;
import com.raincat.core.helper.RedisHelper;
import com.raincat.core.spi.TransactionRecoverRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * redis impl.
 *
 * @author xiaoyu
 */
public class RedisTransactionRecoverRepository implements TransactionRecoverRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisTransactionRecoverRepository.class);

    private ObjectSerializer objectSerializer;

    private String keyName;

    private JedisClient jedisClient;

    @Override
    public int create(final TransactionRecover transactionRecover) {
        try {
            final String redisKey = RedisHelper.buildRecoverKey(keyName, transactionRecover.getId());
            jedisClient.set(redisKey, TransactionRecoverUtils.convert(transactionRecover, objectSerializer));
            return ROWS;
        } catch (Exception e) {
            throw new TransactionIoException(e);
        }
    }

    @Override
    public int remove(final String id) {
        try {
            final String redisKey = RedisHelper.buildRecoverKey(keyName, id);
            return jedisClient.del(redisKey).intValue();
        } catch (Exception e) {
            throw new TransactionIoException(e);
        }
    }

    @Override
    public int update(final TransactionRecover transactionRecover) throws TransactionRuntimeException {
        try {
            final String redisKey = RedisHelper.buildRecoverKey(keyName, transactionRecover.getId());
            if (CompensationOperationTypeEnum.TASK_EXECUTE.getCode() == transactionRecover.getOperation()) {
                TransactionRecover recover = findById(transactionRecover.getId());
                recover.setCompleteFlag(CommonConstant.TX_TRANSACTION_COMPLETE_FLAG_OK);
                jedisClient.set(redisKey, TransactionRecoverUtils.convert(recover, objectSerializer));
                return ROWS;
            }
            transactionRecover.setVersion(transactionRecover.getVersion() + 1);
            transactionRecover.setLastTime(new Date());
            transactionRecover.setRetriedCount(transactionRecover.getRetriedCount() + 1);
            jedisClient.set(redisKey, TransactionRecoverUtils.convert(transactionRecover, objectSerializer));
            return ROWS;
        } catch (Exception e) {
            throw new TransactionRuntimeException(e);
        }
    }

    @Override
    public TransactionRecover findById(final String id) {
        try {
            final String redisKey = RedisHelper.buildRecoverKey(keyName, id);
            byte[] contents = jedisClient.get(redisKey.getBytes());
            return TransactionRecoverUtils.transformBean(contents, objectSerializer);
        } catch (Exception e) {
            throw new TransactionIoException(e);
        }
    }

    @Override
    public List<TransactionRecover> listAll() {
        try {
            List<TransactionRecover> transactions = Lists.newArrayList();
            Set<byte[]> keys = jedisClient.keys((keyName + "*").getBytes());
            for (final byte[] key : keys) {
                byte[] contents = jedisClient.get(key);
                if (contents != null) {
                    transactions.add(TransactionRecoverUtils.transformBean(contents, objectSerializer));
                }
            }
            return transactions;
        } catch (Exception e) {
            throw new TransactionIoException(e);
        }
    }

    @Override
    public List<TransactionRecover> listAllByDelay(final Date date) {
        final List<TransactionRecover> tccTransactions = listAll();
        return tccTransactions
                .stream()
                .filter(transactionRecover -> transactionRecover.getLastTime().compareTo(date) < 0)
                .collect(Collectors.toList());
    }

    @Override
    public void init(final String appName, final TxConfig txConfig) {
        keyName = RepositoryPathUtils.buildRedisKey(appName);
        final TxRedisConfig txRedisConfig = txConfig.getTxRedisConfig();
        try {
            buildJedisClient(txRedisConfig);
        } catch (Exception e) {
            LogUtil.error(LOGGER, "redis init exception please check your config :{}", e::getMessage);
        }
    }

    @Override
    public String getScheme() {
        return CompensationCacheTypeEnum.REDIS.getCompensationCacheType();
    }

    @Override
    public void setSerializer(final ObjectSerializer objectSerializer) {
        this.objectSerializer = objectSerializer;
    }

    private void buildJedisClient(final TxRedisConfig txRedisConfig) {
        JedisPool jedisPool;
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(txRedisConfig.getMaxIdle());
        //最小空闲连接数, 默认0
        config.setMinIdle(txRedisConfig.getMinIdle());
        //最大连接数, 默认8个
        config.setMaxTotal(txRedisConfig.getMaxTotal());
        //获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
        config.setMaxWaitMillis(txRedisConfig.getMaxWaitMillis());
        //在获取连接的时候检查有效性, 默认false
        config.setTestOnBorrow(txRedisConfig.getTestOnBorrow());
        //返回一个jedis实例给连接池时，是否检查连接可用性（ping()）
        config.setTestOnReturn(txRedisConfig.getTestOnReturn());
        //在空闲时检查有效性, 默认false
        config.setTestWhileIdle(txRedisConfig.getTestWhileIdle());
        //逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
        config.setMinEvictableIdleTimeMillis(txRedisConfig.getMinEvictableIdleTimeMillis());
        //对象空闲多久后逐出, 当空闲时间>该值 ，且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断  (默认逐出策略)，默认30m
        config.setSoftMinEvictableIdleTimeMillis(txRedisConfig.getSoftMinEvictableIdleTimeMillis());
        //逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
        config.setTimeBetweenEvictionRunsMillis(txRedisConfig.getTimeBetweenEvictionRunsMillis());
        //每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
        config.setNumTestsPerEvictionRun(txRedisConfig.getNumTestsPerEvictionRun());

        //如果是集群模式
        if (txRedisConfig.getCluster()) {
            final String clusterUrl = txRedisConfig.getClusterUrl();
            final Set<HostAndPort> hostAndPorts = Splitter.on(clusterUrl)
                    .splitToList(";").stream()
                    .map(HostAndPort::parseString).collect(Collectors.toSet());
            JedisCluster jedisCluster = new JedisCluster(hostAndPorts, config);
            jedisClient = new JedisClientCluster(jedisCluster);
        } else {
            if (StringUtils.isNoneBlank(txRedisConfig.getPassword())) {
                jedisPool = new JedisPool(config, txRedisConfig.getHostName(), txRedisConfig.getPort(), txRedisConfig.getTimeOut(), txRedisConfig.getPassword());
            } else {
                jedisPool = new JedisPool(config, txRedisConfig.getHostName(), txRedisConfig.getPort(), txRedisConfig.getTimeOut());
            }
            jedisClient = new JedisClientSingle(jedisPool);
        }
    }
}
