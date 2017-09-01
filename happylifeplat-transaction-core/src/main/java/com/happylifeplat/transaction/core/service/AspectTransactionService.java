package com.happylifeplat.transaction.core.service;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  事务切面接口
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/11 18:31
 * @since JDK 1.8
 */
public interface AspectTransactionService {

    Object invoke(String transactionGroupId, ProceedingJoinPoint point) throws Throwable;
}
