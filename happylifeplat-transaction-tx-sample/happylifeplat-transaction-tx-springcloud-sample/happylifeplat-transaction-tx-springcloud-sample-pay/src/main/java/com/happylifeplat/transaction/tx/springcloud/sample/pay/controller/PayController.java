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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
