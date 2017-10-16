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
package com.happylifeplat.transaction.tx.springcloud.sample.alipay.controller;

import com.happylifeplat.transaction.tx.springcloud.sample.alipay.entity.Alipay;
import com.happylifeplat.transaction.tx.springcloud.sample.alipay.service.AlipayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author xiaoyu
 */
@RestController
@RequestMapping("/alipay")
public class AlipayController {


    private final AlipayService alipayService;

    @Autowired
    public AlipayController(AlipayService alipayService) {
        this.alipayService = alipayService;
    }

    @RequestMapping("/save")
    public int save() {
        Alipay alipay = new Alipay();
        alipay.setAmount(BigDecimal.valueOf(100));
        alipay.setName("ali");
        alipay.setCreateTime(new Date());
        return alipayService.payment(alipay);
    }

    @RequestMapping("/payFail")
    public void payFail() {
        alipayService.payFail();
    }


    @RequestMapping("/payTimeOut")
    public void payTimeOut() {
        Alipay alipay = new Alipay();
        alipay.setAmount(BigDecimal.valueOf(100));
        alipay.setName("ali");
        alipay.setCreateTime(new Date());
        alipayService.payTimeOut(alipay);
    }


}
