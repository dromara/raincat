package com.happylifeplat.transaction.tx.dubbo.sample.consume.service;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/12 11:44
 * @since JDK 1.8
 */
public interface Test1Service {

    String save();

    String testFail();

    /**
     * 强一致性测试
     * 测试 订单保存异常的情况
     * 此时t_test 表不会新增数据 order表不会新增数据 stock则不执行
     * @return "order_fail"
     */
    String testOrderFail();


    /**
     * 强一致性测试
     * 测试 订单保存超时的情况
     * 此时t_test 表不会新增数据,order表不会新增数据 stock则不执行
     * @return "order_timeOut"
     */
    String testOrderTimeOut();



    /**
     * 强一致性测试
     * 测试 stock保存异常的情况
     * 此时t_test 表不会新增数据 order表不会新增数据 stock表不会新增数据
     * @return "stock_fail"
     */
    String testStockFail();


    /**
     * 强一致性测试
     * 测试 stock保存超时的情况
     * 此时t_test 表不会新增数据,order表不会新增数据 stock表不会新增数据
     * @return "stock_timeOut"
     */
    String testStockTimeOut();



}
