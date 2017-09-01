package com.happylifeplat.transaction.tx.springcloud.sample.wechat.controller;

import com.happylifeplat.transaction.tx.springcloud.sample.wechat.entity.Wechat;
import com.happylifeplat.transaction.tx.springcloud.sample.wechat.service.WechatService;
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
@RequestMapping("/wechat")
public class WechatController {


    private final WechatService wechatService;

    @Autowired
    public WechatController(WechatService wechatService) {
        this.wechatService = wechatService;
    }

    @RequestMapping("/save")
    public int save(){
        Wechat wechat = new Wechat();
        wechat.setAmount(BigDecimal.valueOf(100));
        wechat.setName("wechat");
        wechat.setCreateTime(new Date());
        return wechatService.payment(wechat);
    }


    @RequestMapping("/payFail")
    public void payFail(){
         wechatService.payFail();
    }


    @RequestMapping("/payTimeOut")
    public void payTimeOut(){
        Wechat wechat = new Wechat();
        wechat.setAmount(BigDecimal.valueOf(100));
        wechat.setName("wechat");
        wechat.setCreateTime(new Date());
        wechatService.payTimeOut(wechat);
    }


}
