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
package com.happylifeplat.transaction.tx.springcloud.sample.pay.controller;

import com.happylifeplat.transaction.tx.springcloud.sample.pay.service.PayService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaoyu
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    private final PayService payService;

    @Autowired
    public PayController(PayService payService) {
        this.payService = payService;
    }

    @PostMapping("/orderPay")
    public String save() {
        payService.orderPay();
        return "success";
    }

    @PostMapping("/aliPayFail")
    @ApiOperation("当alipay支付异常的时候，pay表的数据不会新增 alipay表不会新增 wechat表不会新增")
    public String aliPayFail() {

        try {
            payService.payWithAliPayFail();
        } catch (Exception e) {
            e.printStackTrace();
            return "aili pay fail and pay not commit";
        }

        return "success";
    }


    @PostMapping("/aliPayTimeOut")
    @ApiOperation("当alipay支付超时的时候，pay表的数据不会新增  alipay表不会新增 wechat表不会新增")
    public String aliPayTimeOut() {
        try {
            payService.payWithAliPayTimeOut();
        } catch (Exception e) {
            e.printStackTrace();
            return "aili pay time out  and pay not commit";
        }

        return "success";
    }


    @PostMapping("/wechatPayFail")
    @ApiOperation("当wechat支付失败的时候，pay表的数据不会新增  alipay表不会新增 wechat表不会新增")
    public String wechatPayFail() {
        try {
            payService.payWithWechatPayFail();
        } catch (Exception e) {
            e.printStackTrace();
            return "wechat pay fail : pay not commit,alipay not commit ";
        }

        return "success";
    }


    @PostMapping("/wechatPayTimeOut")
    @ApiOperation("当wechat支付超时的时候，pay表的数据不会新增  alipay表不会新增 wechat表不会新增")
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
