package com.happylifeplat.transaction.core.interceptor;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * TxTransaction注解的切面
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/11 17:09
 * @since JDK 1.8
 */
@Aspect
public abstract class TxTransactionAspect {

    private TxTransactionInterceptor txTransactionInterceptor;

    public void setTxTransactionInterceptor(TxTransactionInterceptor txTransactionInterceptor) {
        this.txTransactionInterceptor = txTransactionInterceptor;
    }

    @Pointcut("@annotation(com.happylifeplat.transaction.core.annotation.TxTransaction)")
    public void txTransactionInterceptor() {

    }

    @Around("txTransactionInterceptor()")
    public Object interceptCompensableMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return txTransactionInterceptor.interceptor(proceedingJoinPoint);
}

    public abstract int getOrder();
}
