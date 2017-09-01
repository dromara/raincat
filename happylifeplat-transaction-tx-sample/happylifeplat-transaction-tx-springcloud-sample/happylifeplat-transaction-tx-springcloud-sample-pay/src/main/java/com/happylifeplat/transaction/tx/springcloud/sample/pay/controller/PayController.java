package com.happylifeplat.transaction.tx.springcloud.sample.pay.controller;

import com.happylifeplat.transaction.tx.springcloud.sample.pay.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/8/7 13:58
 * @since JDK 1.8
 */

@RestController
@RequestMapping("/pay")
public class PayController {

    private final PayService payService;

    @Autowired
    public PayController(PayService payService) {
        this.payService = payService;
    }

    @RequestMapping("/orderPay")
    public String save() {
        payService.orderPay();
        return "success";
    }

    @RequestMapping("/aliPayFail")
    public String aliPayFail() {

        try {
            payService.payWithAliPayFail();
        } catch (Exception e) {
            e.printStackTrace();
            return "aili pay fail and pay not commit";
        }

        return "success";
    }


    @RequestMapping("/aliPayTimeOut")
    public String aliPayTimeOut() {
        try {
            payService.payWithAliPayTimeOut();
        } catch (Exception e) {
            e.printStackTrace();
            return "aili pay time out  and pay not commit";
        }

        return "success";
    }



    @RequestMapping("/wechatPayFail")
    public String wechatPayFail() {
        try {
            payService.payWithWechatPayFail();
        } catch (Exception e) {
            e.printStackTrace();
            return "wechat pay fail : pay not commit,alipay not commit ";
        }

        return "success";
    }


    @RequestMapping("/wechatPayTimeOut")
    public String wechatPayTimeOut() {
        try {
            payService.payWithWechatPayTimeOut();
        } catch (Exception e) {
            e.printStackTrace();
            return "wechat pay time out : pay not commit,alipay not commit ";
        }

        return "success";
    }




}
