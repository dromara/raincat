package com.happylifeplat.transaction.tx.springcloud.sample.alipay.controller;

import com.happylifeplat.transaction.tx.springcloud.sample.alipay.entity.Alipay;
import com.happylifeplat.transaction.tx.springcloud.sample.alipay.service.AlipayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/8/7 16:05
 * @since JDK 1.8
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
