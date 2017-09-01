package com.happylifeplat.transaction.core.service.handler;

import com.happylifeplat.transaction.core.bean.TxTransactionInfo;
import com.happylifeplat.transaction.core.service.TxTransactionHandler;
import org.aspectj.lang.ProceedingJoinPoint;
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
 * 当分布式事务注解 在一个模块内且套的时候，会进入该handler
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/20 19:25
 * @since JDK 1.8
 */
@Component
public class InsideCompensationHandler implements TxTransactionHandler {


    /**
     * 处理补偿内嵌的远程方法的时候，不提交，只调用
     *
     * @param point point 切点
     * @param info  信息
     * @return Object
     * @throws Throwable
     */
    @Override
    public Object handler(ProceedingJoinPoint point, TxTransactionInfo info) throws Throwable {
        try {
            return point.proceed();
        } catch (Throwable e) {
            throw e;
        }

    }
}
