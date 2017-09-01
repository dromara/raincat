package com.happylifeplat.transaction.core.spi.repository;

import com.google.common.collect.Lists;
import com.happylifeplat.transaction.common.enums.CompensationCacheTypeEnum;
import com.happylifeplat.transaction.common.exception.TransactionException;
import com.happylifeplat.transaction.common.exception.TransactionIOException;
import com.happylifeplat.transaction.common.exception.TransactionRuntimeException;
import com.happylifeplat.transaction.common.holder.LogUtil;
import com.happylifeplat.transaction.core.bean.TransactionRecover;
import com.happylifeplat.transaction.core.config.TxConfig;
import com.happylifeplat.transaction.core.config.TxRedisConfig;
import com.happylifeplat.transaction.core.helper.ByteUtils;
import com.happylifeplat.transaction.core.helper.RedisHelper;
import com.happylifeplat.transaction.core.spi.ObjectSerializer;
import com.happylifeplat.transaction.core.spi.TransactionRecoverRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * redis 实现
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/19 19:33
 * @since JDK 1.8
 */
public class RedisTransactionRecoverRepository implements TransactionRecoverRepository {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisTransactionRecoverRepository.class);


    private ObjectSerializer objectSerializer;


    private JedisPool jedisPool;

    private String keyName;

    /**
     * 创建本地事务对象
     *
     * @param transactionRecover 事务对象
     * @return rows
     */
    @Override
    public int create(TransactionRecover transactionRecover) {
        try {
            final byte[] key = RedisHelper.getRedisKey(keyName, transactionRecover.getId());
            Long statusCode = RedisHelper.execute(jedisPool,
                    jedis -> {
                        try {
                            return jedis.hsetnx(key,
                                    ByteUtils.longToBytes(transactionRecover.getVersion()),
                                    objectSerializer.serialize(transactionRecover));
                        } catch (TransactionException e) {
                            e.printStackTrace();
                            return 0L;
                        }
                    });

            return statusCode.intValue();
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    /**
     * 删除对象
     *
     * @param id 事务对象id
     * @return rows
     */
    @Override
    public int remove(String id) {
        try {
            final byte[] key = RedisHelper.getRedisKey(keyName, id);
            Long result = RedisHelper.execute(jedisPool, jedis -> jedis.del(key));
            return result.intValue();
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    /**
     * 更新数据
     *
     * @param transactionRecover 事务对象
     * @return rows 1 成功 0 失败 失败需要抛异常
     */
    @Override
    public int update(TransactionRecover transactionRecover) throws TransactionRuntimeException {
        try {
            final byte[] key = RedisHelper.getRedisKey(keyName, transactionRecover.getId());
            Long statusCode = RedisHelper.execute(jedisPool, jedis -> {
                transactionRecover.setVersion(transactionRecover.getVersion()+1);
                transactionRecover.setLastTime(new Date());
                transactionRecover.setRetriedCount(transactionRecover.getRetriedCount()+1);
                try {
                    return jedis.hsetnx(key,
                            ByteUtils.longToBytes(transactionRecover.getVersion()),
                            objectSerializer.serialize(transactionRecover));
                } catch (TransactionException e) {
                    e.printStackTrace();
                    return 0L;
                }

            });

            final int intValue = statusCode.intValue();
            if(intValue<=0){
                throw new TransactionRuntimeException("数据已经被更新！");
            }
            return intValue;
        } catch (Exception e) {
            throw new TransactionRuntimeException(e);
        }
    }


    /**
     * 根据id获取对象
     *
     * @param id 主键id
     * @return TransactionRecover
     */
    @Override
    public TransactionRecover findById(String id) {
        try {

            final byte[] key = RedisHelper.getRedisKey(keyName, id);
            byte[] content = RedisHelper.getKeyValue(jedisPool, key);
            if (content != null) {
                return objectSerializer.deSerialize(content, TransactionRecover.class);
            }
            return null;
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    /**
     * 获取需要提交的事务
     *
     * @return List<TransactionRecover>
     */
    @Override
    public List<TransactionRecover> listAll() {
        try {
            List<TransactionRecover> transactions = Lists.newArrayList();
            Set<byte[]> keys = RedisHelper.execute(jedisPool,
                    jedis -> jedis.keys((keyName + "*").getBytes()));
            for (final byte[] key : keys) {
                byte[] content = RedisHelper.getKeyValue(jedisPool, key);
                if (content != null) {
                    transactions.add(objectSerializer.deSerialize(content, TransactionRecover.class));
                }
            }
            return transactions;
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    /**
     * 初始化操作
     *
     * @param modelName 模块名称
     * @param txConfig  配置信息
     */
    @Override
    public void init(String modelName, TxConfig txConfig) {
        keyName = modelName;
        final TxRedisConfig txRedisConfig = txConfig.getTxRedisConfig();
        try {
            buildJedisPool(txRedisConfig);
        } catch (Exception e) {
            LogUtil.error(LOGGER, "redis 初始化异常！请检查配置信息:{}", e::getMessage);
        }
    }


    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    @Override
    public String getScheme() {
        return CompensationCacheTypeEnum.REDIS.getCompensationCacheType();
    }

    /**
     * 设置序列化信息
     *
     * @param objectSerializer 序列化实现
     */
    @Override
    public void setSerializer(ObjectSerializer objectSerializer) {
        this.objectSerializer = objectSerializer;
    }

    private void buildJedisPool(TxRedisConfig txRedisConfig) {
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
        config.setTimeBetweenEvictionRunsMillis(txRedisConfig.getTimeBetweenEvictionRunsMillis()); //1m
        //每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
        config.setNumTestsPerEvictionRun(txRedisConfig.getNumTestsPerEvictionRun());
        if(StringUtils.isNoneBlank(txRedisConfig.getPassword())){
            jedisPool = new JedisPool(config, txRedisConfig.getHostName(), txRedisConfig.getPort(), txRedisConfig.getTimeOut(), txRedisConfig.getPassword());
        }else{
            jedisPool = new JedisPool(config, txRedisConfig.getHostName(), txRedisConfig.getPort(), txRedisConfig.getTimeOut());
        }

    }
}
