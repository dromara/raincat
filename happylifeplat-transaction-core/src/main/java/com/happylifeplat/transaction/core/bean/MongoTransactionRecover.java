package com.happylifeplat.transaction.core.bean;

import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  mongo 保存对象
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/19 20:02
 * @since JDK 1.8
 */
public class MongoTransactionRecover  implements Serializable{

    private static final long serialVersionUID = 7920817865031921102L;


    private ObjectId id;


    /**
     * 事务主键id
     */
    private String transId;


    /**
     * 重试次数，
     */
    private int retriedCount = 0;

    /**
     * 创建时间
     */
    private Date createTime = new Date();


    /**
     * 创建时间
     */
    private Date lastTime = new Date();

    /**
     * 版本控制 防止并发问题
     */
    private int version = 1;

    /**
     * 事务组id
     */
    private String groupId;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 序列化后的二进制信息
     */
    private byte[] contents;

    /**
     *  {@linkplain com.happylifeplat.transaction.common.enums.TransactionStatusEnum}
     */
    private int status;

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
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

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
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

    public byte[] getContents() {
        return contents;
    }

    public void setContents(byte[] contents) {
        this.contents = contents;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
