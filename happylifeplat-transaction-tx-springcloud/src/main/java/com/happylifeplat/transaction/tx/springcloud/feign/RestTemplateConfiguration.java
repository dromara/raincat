package com.happylifeplat.transaction.tx.springcloud.feign;


import feign.Feign;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;


/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * RestTemplateConfiguration
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/11 18:22
 * @since JDK 1.8
 */
@Configuration
public class RestTemplateConfiguration {

    @Bean
    @Scope("prototype")
    public Feign.Builder feignBuilder() {
        return Feign.builder().requestInterceptor(new RestTemplateInterceptor());
    }

}
