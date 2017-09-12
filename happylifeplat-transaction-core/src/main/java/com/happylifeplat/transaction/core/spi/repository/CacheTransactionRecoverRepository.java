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
package com.happylifeplat.transaction.core.spi.repository;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.happylifeplat.transaction.common.exception.TransactionRuntimeException;
import com.happylifeplat.transaction.core.bean.TransactionRecover;
import com.happylifeplat.transaction.core.spi.TransactionRecoverRepository;


import java.util.List;
import java.util.concurrent.TimeUnit;

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
