package com.happylifeplat.transaction.core.bean;

import com.happylifeplat.transaction.core.concurrent.threadlocal.CompensationLocal;
import com.happylifeplat.transaction.core.concurrent.threadlocal.TxTransactionLocal;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/20 15:23
 * @since JDK 1.8
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
