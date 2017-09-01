package com.happylifeplat.transaction.tx.dubbo.sample.consume.controller;

import com.happylifeplat.transaction.common.entity.TxManagerServer;
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
 * @date 2017/8/3 11:34
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/consume/index")
public class IndexController {

    @Autowired
    private Test1Service test1Service;

    @ResponseBody
    @PostMapping("/index")
    public String  findTxManagerServer(){
        return  "hello index";
    }

}
