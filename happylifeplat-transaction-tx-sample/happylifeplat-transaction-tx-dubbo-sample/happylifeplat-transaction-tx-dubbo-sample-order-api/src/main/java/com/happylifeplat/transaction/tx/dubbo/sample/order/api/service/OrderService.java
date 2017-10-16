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
package com.happylifeplat.transaction.tx.dubbo.sample.order.api.service;

import com.happylifeplat.transaction.tx.dubbo.sample.order.api.entity.Order;

/**
 * @author xiaoyu
 */
public interface OrderService {

    /**
     * 保存订单
     *
     * @param order 订单实体
     */
    void save(Order order);

    /**
     * 保存订单失败 抛出异常
     *
     * @param order 订单实体
     * @throws RuntimeException 异常
     */
    void fail(Order order) throws RuntimeException;

    /**
     * 保存订单超时
     *
     * @param order 订单实体
     */
    void timeOut(Order order);


}
