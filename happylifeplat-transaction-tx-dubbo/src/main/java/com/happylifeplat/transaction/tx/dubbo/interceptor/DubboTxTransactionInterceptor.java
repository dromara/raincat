package com.happylifeplat.transaction.tx.dubbo.interceptor;


import com.alibaba.dubbo.rpc.RpcContext;
import com.happylifeplat.transaction.core.interceptor.TxTransactionInterceptor;
import com.happylifeplat.transaction.core.service.AspectTransactionService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * DubboTxTransactionInterceptor 拦截器
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/11 18:22
 * @since JDK 1.8
 */
@Component
public class DubboTxTransactionInterceptor implements TxTransactionInterceptor {

    private final AspectTransactionService aspectTransactionService;

    @Autowired
    public DubboTxTransactionInterceptor(AspectTransactionService aspectTransactionService) {
        this.aspectTransactionService = aspectTransactionService;
    }


    @Override
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {
        String groupId = RpcContext.getContext().getAttachment("tx-group");
        return aspectTransactionService.invoke(groupId,pjp);
    }

}
