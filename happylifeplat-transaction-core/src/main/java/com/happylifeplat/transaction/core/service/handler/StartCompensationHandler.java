package com.happylifeplat.transaction.core.service.handler;

import com.happylifeplat.transaction.common.holder.LogUtil;
import com.happylifeplat.transaction.core.bean.TxTransactionInfo;
import com.happylifeplat.transaction.core.concurrent.threadlocal.CompensationLocal;
import com.happylifeplat.transaction.core.concurrent.threadlocal.TxTransactionLocal;
import com.happylifeplat.transaction.core.constant.Constant;
import com.happylifeplat.transaction.core.service.TxTransactionHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/8/3 19:48
 * @since JDK 1.8
 */
@Component
public class StartCompensationHandler implements TxTransactionHandler {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StartCompensationHandler.class);

    private final PlatformTransactionManager platformTransactionManager;

    @Autowired
    public StartCompensationHandler(PlatformTransactionManager platformTransactionManager) {
        this.platformTransactionManager = platformTransactionManager;
    }

    /**
     * 补偿的时候，不走分布式事务处理
     *
     * @param point point 切点
     * @param info  信息
     * @return Object
     * @throws Throwable
     */
    @Override
    public Object handler(ProceedingJoinPoint point, TxTransactionInfo info) throws Throwable {
        TxTransactionLocal.getInstance().setTxGroupId(Constant.COMPENSATE_ID);
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(def);
        try {
            final Object proceed = point.proceed();
            platformTransactionManager.commit(transactionStatus);
            LogUtil.info(LOGGER,"补偿事务执行成功!事务组id为:{}",info::getTxGroupId);
            return proceed;
        } catch (Throwable e) {
            LogUtil.info(LOGGER,"补偿事务执行失败!事务组id为:{}",info::getTxGroupId);
            platformTransactionManager.rollback(transactionStatus);
            throw e;
        } finally {
            TxTransactionLocal.getInstance().removeTxGroupId();
            CompensationLocal.getInstance().removeCompensationId();
        }
    }
}
