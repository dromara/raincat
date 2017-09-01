package com.happylifeplat.transaction.common.netty.bean;

import io.netty.channel.Channel;

import java.io.Serializable;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * 参与事务的模块
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/20 20:06
 * @since JDK 1.8
 */
public class TxTransactionItem implements Serializable {

    private static final long serialVersionUID = -983809184773470584L;
    /**
     * taskKey
     */
    private String taskKey;

    /**
     * 参与事务id
     */
    private String transId;

    /**
     * 事务状态 {@linkplain com.happylifeplat.transaction.common.enums.TransactionStatusEnum}
     */
    private int status;

    /**
     * 事务角色 {@linkplain com.happylifeplat.transaction.common.enums.TransactionRoleEnum}
     */
    private int role;

    /**
     * 模块信息
     */
    private String modelName;

    /**
     * tm 的域名信息
     */
    private String tmDomain;


    /**
     * 存放事务组id
     */
    private String txGroupId;




    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }


    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getTmDomain() {
        return tmDomain;
    }

    public void setTmDomain(String tmDomain) {
        this.tmDomain = tmDomain;
    }

    public String getTxGroupId() {
        return txGroupId;
    }

    public void setTxGroupId(String txGroupId) {
        this.txGroupId = txGroupId;
    }
}
