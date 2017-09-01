package com.happylifeplat.transaction.tx.dubbo.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * ChannelServiceConfiguration 配置
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/6/27 14:07
 * @since JDK 1.8
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class DubboConfiguration {


    @Configuration
    static class DubboAspectConfiguration {



      /*  private final DubboTxTransactionInterceptor dubboTxTransactionInterceptor;

        @Autowired(required = false)
        public DubboAspectConfiguration(DubboTxTransactionInterceptor dubboTxTransactionInterceptor) {
            this.dubboTxTransactionInterceptor = dubboTxTransactionInterceptor;
        }


        @Bean
        public DubboTxTransactionAspect dubboTxTransactionAspect() {
          //  return new DubboTxTransactionAspect(dubboTxTransactionInterceptor);
        }

*/
    }
}
