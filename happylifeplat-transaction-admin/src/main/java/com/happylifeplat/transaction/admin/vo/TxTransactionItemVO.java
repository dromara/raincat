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
package com.happylifeplat.transaction.admin.vo;

import java.io.Serializable;


/**
 * @author xiaoyu
 */
public class TxTransactionItemVO implements Serializable {


    private static final long serialVersionUID = 7873174484499376766L;
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
    private String status;

    /**
     * 事务角色 {@linkplain com.happylifeplat.transaction.common.enums.TransactionRoleEnum}
     */
    private String role;

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

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 事务最大等待时间 单位秒
     */
    private Integer waitMaxTime;


    /**
     * 执行类名称
     */
    private String targetClazzName;
    /**
     * 执行方法
     */
    private String targetMethodName;

    /**
     * 耗时 秒
     */
    private Long consumeTime;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }




    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Integer getWaitMaxTime() {
        return waitMaxTime;
    }

    public void setWaitMaxTime(Integer waitMaxTime) {
        this.waitMaxTime = waitMaxTime;
    }

    public String getTargetClazzName() {
        return targetClazzName;
    }

    public void setTargetClazzName(String targetClazzName) {
        this.targetClazzName = targetClazzName;
    }

    public String getTargetMethodName() {
        return targetMethodName;
    }

    public void setTargetMethodName(String targetMethodName) {
        this.targetMethodName = targetMethodName;
    }

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



    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
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

    public Long getConsumeTime() {
        return consumeTime;
    }

    public void setConsumeTime(Long consumeTime) {
        this.consumeTime = consumeTime;
    }

    @Override
    public String toString() {
        return "TxTransactionItemVO{" +
                "taskKey='" + taskKey + '\'' +
                ", transId='" + transId + '\'' +
                ", status='" + status + '\'' +
                ", role='" + role + '\'' +
                ", modelName='" + modelName + '\'' +
                ", tmDomain='" + tmDomain + '\'' +
                ", txGroupId='" + txGroupId + '\'' +
                ", createDate='" + createDate + '\'' +
                ", waitMaxTime=" + waitMaxTime +
                ", targetClazzName='" + targetClazzName + '\'' +
                ", targetMethodName='" + targetMethodName + '\'' +
                ", consumeTime=" + consumeTime +
                '}';
    }
}
