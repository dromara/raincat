package com.happylifeplat.transaction.tx.dubbo.sample.order.mapper;

import com.happylifeplat.transaction.tx.dubbo.sample.order.api.entity.Order;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/8/1 15:34
 * @since JDK 1.8
 */
public interface OrderMapper {

    void save(Order order);
}
