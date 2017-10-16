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
package com.happylifeplat.transaction.tx.springcloud.sample.pay.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author xiaoyu
 */
@FeignClient(value = "alipay-service", configuration = MyConfiguration.class)
public interface AlipayClient {

    /**
     * 保存操作
     *
     * @return rows
     */
    @RequestMapping("/alipay-service/alipay/save")
    int save();

    /**
     * 支付失败情况测试
     */
    @RequestMapping("/alipay-service/alipay/payFail")
    void payFail();

    /**
     * 支付超时情况测试
     */
    @RequestMapping("/alipay-service/alipay/payTimeOut")
    void payTimeOut();
}
