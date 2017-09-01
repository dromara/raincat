package com.happylifeplat.transaction.core.concurrent.threadlocal;


/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * TxTransactionThreadLocal
 * 分布式事务本地参与对象
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/11 18:14
 * @since JDK 1.8
 */
public class TxTransactionLocal {

    private static final TxTransactionLocal TX_TRANSACTION_LOCAL = new TxTransactionLocal();

    private TxTransactionLocal() {

    }

    public static TxTransactionLocal getInstance() {
        return TX_TRANSACTION_LOCAL;
    }


    private static final ThreadLocal<String> currentLocal = new ThreadLocal<>();


    public void setTxGroupId(String txGroupId) {
        currentLocal.set(txGroupId);
    }

    public String getTxGroupId() {
        return currentLocal.get();
    }

    public void removeTxGroupId() {
        currentLocal.remove();
    }


}
