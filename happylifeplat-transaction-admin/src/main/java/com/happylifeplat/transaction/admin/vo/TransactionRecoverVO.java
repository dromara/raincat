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
 * <p>Description: .</p>
 *
 * @author xiaoyu(Myth)
 * @version 1.0
 * @date 2017/10/19 16:37
 * @since JDK 1.8
 */
public class TransactionRecoverVO implements Serializable {


    private static final long serialVersionUID = 564418979137349581L;
    private String id;


    /**
     * 重试次数，
     */
    private Integer retriedCount;

    /**
     * 创建时间
     */
    private String createTime;


    /**
     * 创建时间
     */
    private String lastTime;

    /**
     * 版本控制 防止并发问题
     */
    private Integer version;

    /**
     * 事务组id
     */
    private String groupId;

    /**
     * 任务id
     */
    private String taskId;


    /**
     * {@linkplain com.happylifeplat.transaction.common.enums.TransactionStatusEnum}
     */
    private String status;


    /**
     * 执行类名称
     */
    private String targetClazzName;
    /**
     * 执行方法
     */
    private String targetMethodName;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getRetriedCount() {
        return retriedCount;
    }

    public void setRetriedCount(Integer retriedCount) {
        this.retriedCount = retriedCount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    @Override
    public String toString() {
        return "TransactionRecoverVO{" +
                "id='" + id + '\'' +
                ", retriedCount=" + retriedCount +
                ", createTime='" + createTime + '\'' +
                ", lastTime='" + lastTime + '\'' +
                ", version=" + version +
                ", groupId='" + groupId + '\'' +
                ", taskId='" + taskId + '\'' +
                ", status='" + status + '\'' +
                ", targetClazzName='" + targetClazzName + '\'' +
                ", targetMethodName='" + targetMethodName + '\'' +
                '}';
    }
}
