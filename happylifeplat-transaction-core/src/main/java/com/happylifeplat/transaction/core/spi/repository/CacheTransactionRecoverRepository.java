package com.happylifeplat.transaction.core.spi.repository;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.happylifeplat.transaction.common.exception.TransactionRuntimeException;
import com.happylifeplat.transaction.core.bean.TransactionRecover;
import com.happylifeplat.transaction.core.spi.TransactionRecoverRepository;


import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>Description: .</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  资源模板类
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @since JDK 1.8
 */
public abstract class CacheTransactionRecoverRepository  implements TransactionRecoverRepository {

    private int expireDuration = 120;

    private Cache<String,TransactionRecover> transactionRecoverCache;


    protected abstract int doCreate(TransactionRecover transactionRecover);

    protected abstract int doUpdate(TransactionRecover transactionRecover);

    protected abstract int doDelete(TransactionRecover transactionRecover);

    protected abstract TransactionRecover doFindOne(String id);

    protected abstract List<TransactionRecover> doListAll();

    public CacheTransactionRecoverRepository() {
        transactionRecoverCache = CacheBuilder.newBuilder().expireAfterAccess(expireDuration, TimeUnit.SECONDS).maximumSize(1000).build();
    }

    protected void putToCache(TransactionRecover transactionRecover) {
        transactionRecoverCache.put(transactionRecover.getId(), transactionRecover);
    }

    protected void removeFromCache(TransactionRecover transactionRecover) {
        transactionRecoverCache.invalidate(transactionRecover.getId());
    }

    protected TransactionRecover findFromCache(String id) {
        return transactionRecoverCache.getIfPresent(id);
    }


    @Override
    public int create(TransactionRecover transactionRecover) {
        int result = doCreate(transactionRecover);
        if (result > 0) {
            putToCache(transactionRecover);
        }
        return result;
    }


    @Override
    public TransactionRecover findById(String id) {
        TransactionRecover tccTransaction = findFromCache(id);

        if (tccTransaction == null) {
            tccTransaction = doFindOne(id);

            if (tccTransaction != null) {
                putToCache(tccTransaction);
            }
        }

        return tccTransaction;
    }

    @Override
    public List<TransactionRecover> listAll() {

        List<TransactionRecover> transactions = doListAll();

        for (TransactionRecover transaction : transactions) {
            putToCache(transaction);
        }

        return transactions;
    }


    public int getExpireDuration() {
        return expireDuration;
    }

    public void setExpireDuration(int expireDuration) {
        this.expireDuration = expireDuration;
    }




}
