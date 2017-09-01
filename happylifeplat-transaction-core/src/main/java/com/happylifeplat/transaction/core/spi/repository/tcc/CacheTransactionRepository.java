package com.happylifeplat.transaction.core.spi.repository.tcc;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.happylifeplat.transaction.common.exception.TransactionRuntimeException;
import com.happylifeplat.transaction.core.bean.tcc.TccTransaction;
import com.happylifeplat.transaction.core.bean.TransactionXid;

import javax.transaction.xa.Xid;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  事务操作模板类
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/5/27 13:51
 * @since JDK 1.8
 */
public abstract class CacheTransactionRepository implements TransactionRepository {

    private int expireDuration = 120;

    private Cache<Xid, TccTransaction> transactionXidCompensableTransactionCache;

    @Override
    public int create(TccTransaction tccTransaction) {
        int result = doCreate(tccTransaction);
        if (result > 0) {
            putToCache(tccTransaction);
        }
        return result;
    }

    @Override
    public int update(TccTransaction tccTransaction) {
        int result = 0;

        try {
            result = doUpdate(tccTransaction);
            if (result > 0) {
                putToCache(tccTransaction);
            } else {
                throw new TransactionRuntimeException();
            }
        } finally {
            if (result <= 0) {
                removeFromCache(tccTransaction);
            }
        }

        return result;
    }

    @Override
    public int delete(TccTransaction tccTransaction) {
        int result;

        try {
            result = doDelete(tccTransaction);

        } finally {
            removeFromCache(tccTransaction);
        }
        return result;
    }

    @Override
    public TccTransaction findByXid(TransactionXid transactionXid) {
        TccTransaction tccTransaction = findFromCache(transactionXid);

        if (tccTransaction == null) {
            tccTransaction = doFindOne(transactionXid);

            if (tccTransaction != null) {
                putToCache(tccTransaction);
            }
        }

        return tccTransaction;
    }

    @Override
    public List<TccTransaction> findAllUnmodifiedSince(Date date) {

        List<TccTransaction> transactions = doFindAllUnmodifiedSince(date);

        for (TccTransaction transaction : transactions) {
            putToCache(transaction);
        }

        return transactions;
    }

    public CacheTransactionRepository() {
        transactionXidCompensableTransactionCache = CacheBuilder.newBuilder().expireAfterAccess(expireDuration, TimeUnit.SECONDS).maximumSize(1000).build();
    }

    protected void putToCache(TccTransaction tccTransaction) {
        transactionXidCompensableTransactionCache.put(tccTransaction.getXid(), tccTransaction);
    }

    protected void removeFromCache(TccTransaction tccTransaction) {
        transactionXidCompensableTransactionCache.invalidate(tccTransaction.getXid());
    }

    protected TccTransaction findFromCache(TransactionXid transactionXid) {
        return transactionXidCompensableTransactionCache.getIfPresent(transactionXid);
    }

    public void setExpireDuration(int durationInSeconds) {
        this.expireDuration = durationInSeconds;
    }

    protected abstract int doCreate(TccTransaction tccTransaction);

    protected abstract int doUpdate(TccTransaction tccTransaction);

    protected abstract int doDelete(TccTransaction tccTransaction);

    protected abstract TccTransaction doFindOne(Xid xid);

    protected abstract List<TccTransaction> doFindAllUnmodifiedSince(Date date);
}
