package com.happylifeplat.transaction.tx.manager.controller;

import com.happylifeplat.transaction.tx.manager.entity.TxManagerInfo;
import com.happylifeplat.transaction.tx.manager.service.TxManagerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;


/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * TxManagerController rest 接口
 * index
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/13 17:27
 * @since JDK 1.8
 */
@Controller
public class TxManagerIndexController {

    private final TxManagerInfoService txManagerInfoService;

    @Autowired
    public TxManagerIndexController(TxManagerInfoService txManagerInfoService) {
        this.txManagerInfoService = txManagerInfoService;
    }


    @RequestMapping("/index")
    public String index(HttpServletRequest request) {
        final TxManagerInfo txManagerInfo = txManagerInfoService.findTxManagerInfo();
        request.setAttribute("info", txManagerInfo);
        return "index";
    }


}
