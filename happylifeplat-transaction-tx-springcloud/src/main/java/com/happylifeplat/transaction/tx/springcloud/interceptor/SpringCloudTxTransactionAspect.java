package com.happylifeplat.transaction.tx.springcloud.interceptor;

import com.happylifeplat.transaction.core.interceptor.TxTransactionAspect;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * SpringCloud 切面
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/11 18:22
 * @since JDK 1.8
 */
@Aspect
@Component
public class SpringCloudTxTransactionAspect extends TxTransactionAspect implements Ordered {


    @Autowired
    public SpringCloudTxTransactionAspect(SpringCloudTxTransactionInterceptor  springCloudTxTransactionInterceptor) {
        this.setTxTransactionInterceptor(springCloudTxTransactionInterceptor);
    }


    public void init() {

    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
