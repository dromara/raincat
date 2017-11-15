/*
 *
 * Copyright 2017-2018 549477611@qq.com(xiaoyu)
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.happylifeplat.transaction.core.service.handler;

import com.happylifeplat.transaction.common.bean.TxTransactionInfo;
import com.happylifeplat.transaction.core.service.TxTransactionHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * @author xiaoyu
 */
@Component
public class InsideCompensationHandler implements TxTransactionHandler {


    private final PlatformTransactionManager platformTransactionManager;

    @Autowired
    public InsideCompensationHandler(PlatformTransactionManager platformTransactionManager) {
        this.platformTransactionManager = platformTransactionManager;
    }

    /**
     * 处理补偿内嵌的远程方法的时候，不提交，只调用
     *
     * @param point point 切点
     * @param info  信息
     * @return Object
     * @throws Throwable 异常
     */
    @Override
    public Object handler(ProceedingJoinPoint point, TxTransactionInfo info) throws Throwable {

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(def);
        try {
            return point.proceed();
        } finally {
            platformTransactionManager.rollback(transactionStatus);
        }

    }
}
