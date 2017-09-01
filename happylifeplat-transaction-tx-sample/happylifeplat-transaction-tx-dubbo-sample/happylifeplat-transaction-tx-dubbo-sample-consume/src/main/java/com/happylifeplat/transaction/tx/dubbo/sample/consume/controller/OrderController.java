package com.happylifeplat.transaction.tx.dubbo.sample.consume.controller;

import com.happylifeplat.transaction.tx.dubbo.sample.consume.service.Test1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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
 * @date 2017/8/1 16:03
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/consume/order")
public class OrderController {

    @Autowired
    private Test1Service test1Service;

    @ResponseBody
    @PostMapping("/save")
    public String save() {
        return test1Service.save();
    }


    @ResponseBody
    @PostMapping("/orderFail")
    public String orderFail() {
        try {
            test1Service.testOrderFail();
        } catch (Exception e) {
            return "orderFail rollback";
        }
        return "orderFail";
    }

    @ResponseBody
    @PostMapping("/orderTimeOut")
    public String orderTimeOut() {

        try {
            test1Service.testOrderTimeOut();
        } catch (Exception e) {
            e.printStackTrace();
            return "orderTimeOut  rollback";
        }
        return "orderTimeOut";
    }


    @ResponseBody
    @PostMapping("/stockFail")
    public String stockFail() {
        try {
            test1Service.testStockFail();
        } catch (Exception e) {
            e.printStackTrace();
            return "stockFail  rollback";
        }
        return "stockFail";

    }

    @ResponseBody
    @PostMapping("/stockTimeOut")
    public String stockTimeOut() {

        try {
            test1Service.testStockTimeOut();
        } catch (Exception e) {
            e.printStackTrace();
            return "stockTimeOut  rollback";
        }
        return "stockTimeOut";

    }


}
