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
package com.happylifeplat.transaction.core.bean;

/**
 * @author xiaoyu
 */
public class TxTransactionInfo {

    /**
     * 补偿方法对象
     */
    private TransactionInvocation invocation;


    /**
     * 分布式事务组
     */
    private String txGroupId;

    private String compensationId;

    public TxTransactionInfo(String txGroupId, TransactionInvocation invocation) {
        this.txGroupId = txGroupId;
        this.invocation = invocation;
    }

    public TxTransactionInfo(
            String compensationId, String txGroupId,
            TransactionInvocation invocation) {
        this.compensationId = compensationId;
        this.txGroupId = txGroupId;
        this.invocation = invocation;
    }


    public TransactionInvocation getInvocation() {
        return invocation;
    }

    public String getTxGroupId() {
        return txGroupId;
    }


    public String getCompensationId() {
        return compensationId;
    }
}
