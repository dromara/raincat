package com.happylifeplat.transaction.tx.springcloud.sample.pay.service.impl;

import com.happylifeplat.transaction.core.annotation.TxTransaction;
import com.happylifeplat.transaction.tx.springcloud.sample.pay.client.AlipayClient;
import com.happylifeplat.transaction.tx.springcloud.sample.pay.client.WechatClient;
import com.happylifeplat.transaction.tx.springcloud.sample.pay.entiy.Pay;
import com.happylifeplat.transaction.tx.springcloud.sample.pay.mapper.PayMapper;
import com.happylifeplat.transaction.tx.springcloud.sample.pay.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/8/7 14:02
 * @since JDK 1.8
 */
@Service
public class PayServiceImpl implements PayService{

    private final AlipayClient alipayClient;

    private final WechatClient wechatClient;

    private final PayMapper payMapper;

    @Autowired
    public PayServiceImpl(AlipayClient alipayClient, WechatClient wechatClient, PayMapper payMapper) {
        this.alipayClient = alipayClient;
        this.wechatClient = wechatClient;
        this.payMapper = payMapper;
    }

    @Override
    @TxTransaction
    public Boolean orderPay() {
        Pay pay  = new Pay();
        pay.setName("ali|| wechat");
        pay.setTotalAmount(BigDecimal.valueOf(200));
        pay.setCreateTime(new Date());
        payMapper.save(pay);
        alipayClient.save();

        wechatClient.save();

        return true;
    }

    /**
     * 强一致性测试 执行顺序 pay-->alipay-->wechat
     * 当alipay支付异常的时候，pay表的数据不会新增 alipay表不会新增 wechat表不会新增
     */
    @Override
    @TxTransaction
    public void payWithAliPayFail() {

        Pay pay  = new Pay();
        pay.setName("ali|| wechat");
        pay.setTotalAmount(BigDecimal.valueOf(200));
        pay.setCreateTime(new Date());
        payMapper.save(pay);

        alipayClient.payFail();

        wechatClient.save();

    }

    /**
     * 强一致性测试 执行顺序 pay-->alipay-->wechat
     * 当alipay支付超时的时候，pay表的数据不会新增  alipay表不会新增 wechat表不会新增
     */
    @Override
    @TxTransaction
    public void payWithAliPayTimeOut() {
        Pay pay  = new Pay();
        pay.setName("ali|| wechat");
        pay.setTotalAmount(BigDecimal.valueOf(200));
        pay.setCreateTime(new Date());
        payMapper.save(pay);

        alipayClient.payTimeOut();

        wechatClient.save();
    }

    /**
     * 强一致性测试 执行顺序 pay-->alipay-->wechat
     * 当wechat支付失败的时候，pay表的数据不会新增  alipay表不会新增 wechat表不会新增
     */
    @Override
    @TxTransaction
    public void payWithWechatPayFail() {
        Pay pay  = new Pay();
        pay.setName("ali|| wechat");
        pay.setTotalAmount(BigDecimal.valueOf(200));
        pay.setCreateTime(new Date());
        payMapper.save(pay);

        alipayClient.save();

        wechatClient.payFail();

    }

    /**
     * 强一致性测试 执行顺序 pay-->alipay-->wechat
     * 当wechat支付超时的时候，pay表的数据不会新增  alipay表不会新增 wechat表不会新增
     */
    @Override
    @TxTransaction
    public void payWithWechatPayTimeOut() {

        Pay pay  = new Pay();
        pay.setName("ali|| wechat");
        pay.setTotalAmount(BigDecimal.valueOf(200));
        pay.setCreateTime(new Date());
        payMapper.save(pay);

        alipayClient.save();

        wechatClient.payTimeOut();

    }
}
