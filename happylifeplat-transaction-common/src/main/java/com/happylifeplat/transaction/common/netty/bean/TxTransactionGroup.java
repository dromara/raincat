package com.happylifeplat.transaction.common.netty.bean;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  事务组
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/20 20:05
 * @since JDK 1.8
 */
public class TxTransactionGroup implements Serializable {


    private static final long serialVersionUID = -8826219545126676832L;

    /**
     * 事务组id
     */
    private String id;

    /**
     * 事务等待时间
     */
    private int waitTime;

    /**
     * 事务状态 {@linkplain com.happylifeplat.transaction.common.enums.TransactionStatusEnum}
     */
    private int status;

    private  List<TxTransactionItem> itemList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<TxTransactionItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TxTransactionItem> itemList) {
        this.itemList = itemList;
    }
}
