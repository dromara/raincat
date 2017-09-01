package com.happylifeplat.transaction.core.bean.tcc;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.happylifeplat.transaction.common.enums.TransactionStatusEnum;
import com.happylifeplat.transaction.common.enums.TransactionTypeEnum;
import com.happylifeplat.transaction.core.bean.TransactionXid;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  TccTransaction tcc事务对象
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/5/27 13:51
 * @since JDK 1.8
 */
public class TccTransaction implements Serializable {

    private static final long serialVersionUID = 7291423944314337931L;

    private TransactionXid xid;

    private TransactionStatusEnum transactionStatusEnum;

    private TransactionTypeEnum transactionTypeEnum;

    private volatile int retriedCount = 0;

    private Date createTime = new Date();

    private Date lastUpdateTime = new Date();

    private long version = 1;

    private List<TccParticipant> participants= Lists.newCopyOnWriteArrayList();

    private Map<String, Object> attachments = Maps.newConcurrentMap();

    public TccTransaction() {

    }

    public TransactionXid getXid() {
        return xid;
    }

    public void setXid(TransactionXid xid) {
        this.xid = xid;
    }

    public TransactionStatusEnum getTransactionStatusEnum() {
        return transactionStatusEnum;
    }

    public void setTransactionStatusEnum(TransactionStatusEnum transactionStatusEnum) {
        this.transactionStatusEnum = transactionStatusEnum;
    }

    public TransactionTypeEnum getTransactionTypeEnum() {
        return transactionTypeEnum;
    }

    public void setTransactionTypeEnum(TransactionTypeEnum transactionTypeEnum) {
        this.transactionTypeEnum = transactionTypeEnum;
    }

    public int getRetriedCount() {
        return retriedCount;
    }

    public void setRetriedCount(int retriedCount) {
        this.retriedCount = retriedCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
