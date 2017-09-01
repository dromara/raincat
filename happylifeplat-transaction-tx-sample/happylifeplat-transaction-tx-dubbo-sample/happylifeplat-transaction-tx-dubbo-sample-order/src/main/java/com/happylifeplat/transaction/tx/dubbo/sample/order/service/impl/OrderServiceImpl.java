package com.happylifeplat.transaction.tx.dubbo.sample.order.service.impl;

import com.happylifeplat.transaction.core.annotation.TxTransaction;
import com.happylifeplat.transaction.tx.dubbo.sample.order.api.entity.Order;
import com.happylifeplat.transaction.tx.dubbo.sample.order.api.service.OrderService;
import com.happylifeplat.transaction.tx.dubbo.sample.order.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/8/1 15:35
 * @since JDK 1.8
 */
@Service("orderService")
public class OrderServiceImpl implements OrderService {


    private final OrderMapper orderMapper;

    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }


    @Override
    @TxTransaction
    public void save(Order order) {
        orderMapper.save(order);
    }

    @Override
    @TxTransaction
    public void fail(Order order) throws RuntimeException {
        orderMapper.save(null);
    }

    @Override
    @TxTransaction
    public void timeOut(Order order) {
        //正常保存
        orderMapper.save(order);
        try {
            //模拟超时
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
