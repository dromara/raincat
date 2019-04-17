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

package org.dromara.raincat.core.spi.repository;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.dromara.raincat.annotation.RaincatSPI;
import org.dromara.raincat.common.bean.TransactionRecover;
import org.dromara.raincat.common.config.TxConfig;
import org.dromara.raincat.common.config.TxRedisConfig;
import org.dromara.raincat.common.constant.CommonConstant;
import org.dromara.raincat.common.enums.CompensationOperationTypeEnum;
import org.dromara.raincat.common.exception.TransactionIoException;
import org.dromara.raincat.common.exception.TransactionRuntimeException;
import org.dromara.raincat.common.holder.LogUtil;
import org.dromara.raincat.common.holder.RepositoryPathUtils;
import org.dromara.raincat.common.holder.TransactionRecoverUtils;
import org.dromara.raincat.common.jedis.JedisClient;
import org.dromara.raincat.common.jedis.JedisClientCluster;
import org.dromara.raincat.common.jedis.JedisClientSentinel;
import org.dromara.raincat.common.jedis.JedisClientSingle;
import org.dromara.raincat.common.serializer.ObjectSerializer;
import org.dromara.raincat.core.helper.RedisHelper;
import org.dromara.raincat.core.spi.TransactionRecoverRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * redis impl.
 *
 * @author xiaoyu
 */
@RaincatSPI("redis")
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
    public void setSerializer(final ObjectSerializer objectSerializer) {
        this.objectSerializer = objectSerializer;
    }

    private void buildJedisClient(final TxRedisConfig txRedisConfig) {
        JedisPoolConfig config = new JedisPoolConfig();
        JedisPool jedisPool;
        if (txRedisConfig.getCluster()) {
            LogUtil.info(LOGGER, () -> "build tx redis cluster ............");
            final String clusterUrl = txRedisConfig.getClusterUrl();
            final Set<HostAndPort> hostAndPorts =
                    Splitter.on(";")
                            .splitToList(clusterUrl)
                            .stream()
                            .map(HostAndPort::parseString).collect(Collectors.toSet());
            JedisCluster jedisCluster = new JedisCluster(hostAndPorts, config);
            jedisClient = new JedisClientCluster(jedisCluster);
        } else if (txRedisConfig.getSentinel()) {
            LogUtil.info(LOGGER, () -> "build tx redis sentinel ............");
            final String sentinelUrl = txRedisConfig.getSentinelUrl();
            final Set<String> hostAndPorts =
                    new HashSet<>(Splitter.on(";")
                            .splitToList(sentinelUrl));

            JedisSentinelPool pool =
                    new JedisSentinelPool(txRedisConfig.getMasterName(), hostAndPorts,
                            config, txRedisConfig.getTimeOut(), txRedisConfig.getPassword());
            jedisClient = new JedisClientSentinel(pool);
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
