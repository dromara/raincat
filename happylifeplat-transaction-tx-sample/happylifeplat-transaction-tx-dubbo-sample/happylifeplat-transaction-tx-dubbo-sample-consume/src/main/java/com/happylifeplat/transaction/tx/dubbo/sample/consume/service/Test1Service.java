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
package com.happylifeplat.transaction.tx.dubbo.sample.consume.service;

/**
 * @author xiaoyu
 */
public interface Test1Service {

    /**
     * 保存
     *
     * @return string
     */
    String save();

    /**
     * 保存失败
     *
     * @return String
     */
    String testFail();

    /**
     * 强一致性测试
     * 测试 订单保存异常的情况
     * 此时t_test 表不会新增数据 order表不会新增数据 stock则不执行
     *
     * @return "order_fail"
     */
    String testOrderFail();


    /**
     * 强一致性测试
     * 测试 订单保存超时的情况
     * 此时t_test 表不会新增数据,order表不会新增数据 stock则不执行
     *
     * @return "order_timeOut"
     */
    String testOrderTimeOut();


    /**
     * 强一致性测试
     * 测试 stock保存异常的情况
     * 此时t_test 表不会新增数据 order表不会新增数据 stock表不会新增数据
     *
     * @return "stock_fail"
     */
    String testStockFail();


    /**
     * 强一致性测试
     * 测试 stock保存超时的情况
     * 此时t_test 表不会新增数据,order表不会新增数据 stock表不会新增数据
     *
     * @return "stock_timeOut"
     */
    String testStockTimeOut();


}
