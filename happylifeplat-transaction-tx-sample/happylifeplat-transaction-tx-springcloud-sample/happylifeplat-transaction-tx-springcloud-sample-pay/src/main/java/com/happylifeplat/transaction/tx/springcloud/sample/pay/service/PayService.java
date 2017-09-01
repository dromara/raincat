package com.happylifeplat.transaction.tx.springcloud.sample.pay.service;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/8/7 14:03
 * @since JDK 1.8
 */
public interface PayService {

    /**
     * 正常支付 这时候 pay  alipay wechat 表都会新增一条数据
     *
     * @return Boolean
     */
    Boolean orderPay();

    /**
     *强一致性测试 执行顺序 pay-->alipay-->wechat
     * 当alipay支付异常的时候，pay表的数据不会新增 alipay表不会新增 wechat表不会新增
     */
    void payWithAliPayFail();


    /**
     *强一致性测试 执行顺序 pay-->alipay-->wechat
     * 当alipay支付超时的时候，pay表的数据不会新增  alipay表不会新增 wechat表不会新增
     */
    void payWithAliPayTimeOut();


    /**
     *强一致性测试 执行顺序 pay-->alipay-->wechat
     * 当wechat支付失败的时候，pay表的数据不会新增  alipay表不会新增 wechat表不会新增
     */
    void payWithWechatPayFail();


    /**
     *强一致性测试 执行顺序 pay-->alipay-->wechat
     * 当wechat支付超时的时候，pay表的数据不会新增  alipay表不会新增 wechat表不会新增
     */
    void payWithWechatPayTimeOut();
}
