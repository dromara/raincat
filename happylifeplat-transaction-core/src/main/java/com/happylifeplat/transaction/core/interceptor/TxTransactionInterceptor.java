package com.happylifeplat.transaction.core.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * Tx分布式事务拦截器接口
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/11 17:59
 * @since JDK 1.8
 */
@FunctionalInterface
public interface TxTransactionInterceptor {

    Object interceptor(ProceedingJoinPoint pjp) throws Throwable;
}
