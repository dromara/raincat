package com.happylifeplat.transaction.core.service;

import com.happylifeplat.transaction.core.bean.TxTransactionInfo;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * txTransaction 处理接口
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/20 15:43
 * @since JDK 1.8
 */
@FunctionalInterface
public interface TxTransactionHandler {

    /**
     * 分布式事务处理接口
     *
     * @param point point 切点
     * @param info  信息
     * @return Object
     * @throws Throwable
     */
    Object handler(ProceedingJoinPoint point, TxTransactionInfo info) throws Throwable;
}
