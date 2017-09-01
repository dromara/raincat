package com.happylifeplat.transaction.tx.springcloud.interceptor;


import com.happylifeplat.transaction.core.concurrent.threadlocal.CompensationLocal;
import com.happylifeplat.transaction.core.interceptor.TxTransactionInterceptor;
import com.happylifeplat.transaction.core.service.AspectTransactionService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * SpringCloudTxTransactionInterceptor 拦截器
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/11 18:22
 * @since JDK 1.8
 */
@Component
public class SpringCloudTxTransactionInterceptor implements TxTransactionInterceptor {

    private final AspectTransactionService aspectTransactionService;

    @Autowired
    public SpringCloudTxTransactionInterceptor(AspectTransactionService aspectTransactionService) {
        this.aspectTransactionService = aspectTransactionService;
    }


    @Override
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {
        final String compensationId = CompensationLocal.getInstance().getCompensationId();
        String groupId=null;
        if (StringUtils.isBlank(compensationId)) {
            //如果不是本地反射调用补偿
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = requestAttributes == null ? null : ((ServletRequestAttributes) requestAttributes).getRequest();
            groupId = request == null ? null : request.getHeader("tx-group");
        }

        return aspectTransactionService.invoke(groupId, pjp);
    }

}
