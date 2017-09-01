package com.happylifeplat.transaction.core.spi.repository.tcc;

import com.happylifeplat.transaction.core.bean.tcc.TccTransaction;
import com.happylifeplat.transaction.core.spi.serializer.JavaSerializer;
import com.happylifeplat.transaction.core.spi.ObjectSerializer;
import redis.clients.jedis.JedisPool;

import javax.transaction.xa.Xid;
import java.util.Date;
import java.util.List;


/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  redis实现类
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/5/27 13:51
 * @since JDK 1.8
 */
public class RedisTransactionRepository extends CacheTransactionRepository {

    private JedisPool jedisPool;


    private ObjectSerializer serializer = new JavaSerializer();

    public void setSerializer(ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }


    @Override
    protected int doCreate(TccTransaction tccTransaction) {
        return 0;
    }

    @Override
    protected int doUpdate(TccTransaction tccTransaction) {
        return 0;
    }

    @Override
    protected int doDelete(TccTransaction tccTransaction) {
        return 0;
    }

    @Override
    protected TccTransaction doFindOne(Xid xid) {
        return null;
    }

    @Override
    protected List<TccTransaction> doFindAllUnmodifiedSince(Date date) {
        return null;
    }
}
