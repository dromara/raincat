package com.happylifeplat.transaction.core.service;

import com.happylifeplat.transaction.core.bean.TxTransactionInfo;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * 生成不同实现的 TxTransactionService
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/20 15:42
 * @since JDK 1.8
 */
@FunctionalInterface
public interface TxTransactionFactoryService<T> {

    /**
     * 返回 实现TxTransactionHandler类的名称
     * @param info
     * @return Class<T>
     * @throws Throwable 抛出异常
     */
    Class<T> factoryOf(TxTransactionInfo info) throws Throwable;
}
