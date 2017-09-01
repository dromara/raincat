package com.happylifeplat.transaction.tx.springcloud.sample.pay.client;

import com.happylifeplat.transaction.tx.springcloud.feign.RestTemplateConfiguration;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/8/7 15:33
 * @since JDK 1.8
 */
@FeignClient(value = "wechat-service",configuration =MyConfiguration.class)
public interface WechatClient {

    @RequestMapping("/wechat-service/wechat/save")
    int save();

    @RequestMapping("/wechat-service/wechat/payFail")
    void payFail();

    @RequestMapping("/wechat-service/wechat/payTimeOut")
    void payTimeOut();
}
