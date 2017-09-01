package com.happylifeplat.transaction.tx.dubbo.sample.order.api.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  订单实体
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/8/1 15:29
 * @since JDK 1.8
 */
public class Order  implements Serializable{

    private Integer  id;

    //创建日期
    private Date createTime;

    //订单编号
    private String number;

    //订单类型
    private Integer type;

    private Integer status;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
