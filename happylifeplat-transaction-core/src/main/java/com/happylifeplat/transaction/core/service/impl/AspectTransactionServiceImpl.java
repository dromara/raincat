package com.happylifeplat.transaction.core.service.impl;

import com.happylifeplat.transaction.core.bean.TransactionInvocation;
import com.happylifeplat.transaction.core.bean.TxTransactionInfo;
import com.happylifeplat.transaction.core.concurrent.threadlocal.CompensationLocal;
import com.happylifeplat.transaction.core.helper.SpringBeanUtils;
import com.happylifeplat.transaction.core.service.AspectTransactionService;
import com.happylifeplat.transaction.core.service.TxTransactionFactoryService;
import com.happylifeplat.transaction.core.service.TxTransactionHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * 切面实现类
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/11 18:45
 * @since JDK 1.8
 */
@Service
public class AspectTransactionServiceImpl implements AspectTransactionService {

    private final TxTransactionFactoryService txTransactionFactoryService;

    @Autowired
    public AspectTransactionServiceImpl(TxTransactionFactoryService txTransactionFactoryService) {
        this.txTransactionFactoryService = txTransactionFactoryService;
    }

    @Override
    public Object invoke(String transactionGroupId, ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> clazz = point.getTarget().getClass();
        Object[] args = point.getArgs();
        Method thisMethod = clazz.getMethod(method.getName(), method.getParameterTypes());

        final String compensationId = CompensationLocal.getInstance().getCompensationId();

        TransactionInvocation invocation = new TransactionInvocation(clazz, thisMethod.getName(), args, method.getParameterTypes());
        TxTransactionInfo info = new TxTransactionInfo(compensationId, transactionGroupId, invocation);
        final Class c = txTransactionFactoryService.factoryOf(info);
        final TxTransactionHandler txTransactionHandler =
                (TxTransactionHandler) SpringBeanUtils.getInstance().getBean(c);
        return txTransactionHandler.handler(point, info);
    }
}
