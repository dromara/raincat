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
package com.happylifeplat.transaction.tx.springcloud.sample.pay.service;

/**
 * @author xiaoyu
 */
public interface PayService {

    /**
     * 正常支付 这时候 pay  alipay wechat 表都会新增一条数据
     *
     * @return Boolean
     */
    Boolean orderPay();

    /**
     * 强一致性测试 执行顺序 pay-->alipay-->wechat
     * 当alipay支付异常的时候，pay表的数据不会新增 alipay表不会新增 wechat表不会新增
     */
    void payWithAliPayFail();


    /**
     * 强一致性测试 执行顺序 pay-->alipay-->wechat
     * 当alipay支付超时的时候，pay表的数据不会新增  alipay表不会新增 wechat表不会新增
     */
    void payWithAliPayTimeOut();


    /**
     * 强一致性测试 执行顺序 pay-->alipay-->wechat
     * 当wechat支付失败的时候，pay表的数据不会新增  alipay表不会新增 wechat表不会新增
     */
    void payWithWechatPayFail();


    /**
     * 强一致性测试 执行顺序 pay-->alipay-->wechat
     * 当wechat支付超时的时候，pay表的数据不会新增  alipay表不会新增 wechat表不会新增
     */
    void payWithWechatPayTimeOut();
}
