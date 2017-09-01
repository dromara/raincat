package com.happylifeplat.transaction.tx.dubbo.sample.order.api.service;

import com.happylifeplat.transaction.tx.dubbo.sample.order.api.entity.Order;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/8/1 15:32
 * @since JDK 1.8
 */
public interface OrderService {

    void save(Order order);

    void fail(Order order) throws RuntimeException;

    void timeOut(Order order);


}
