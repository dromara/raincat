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

package org.dromara.raincat.core.service.impl;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.dromara.raincat.annotation.PropagationEnum;
import org.dromara.raincat.annotation.TxTransaction;
import org.dromara.raincat.common.bean.TransactionInvocation;
import org.dromara.raincat.common.bean.TxTransactionInfo;
import org.dromara.raincat.core.concurrent.threadlocal.CompensationLocal;
import org.dromara.raincat.core.helper.SpringBeanUtils;
import org.dromara.raincat.core.service.AspectTransactionService;
import org.dromara.raincat.core.service.TxTransactionFactoryService;
import org.dromara.raincat.core.service.TxTransactionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

/**
 * AspectTransactionServiceImpl.
 * @author xiaoyu
 */
@Service
public class AspectTransactionServiceImpl implements AspectTransactionService {

    private final TxTransactionFactoryService txTransactionFactoryService;

    @Autowired
    public AspectTransactionServiceImpl(final TxTransactionFactoryService txTransactionFactoryService) {
        this.txTransactionFactoryService = txTransactionFactoryService;
    }

    @Override
    public Object invoke(final String transactionGroupId, final ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> clazz = point.getTarget().getClass();
        Object[] args = point.getArgs();
        Method thisMethod = clazz.getMethod(method.getName(), method.getParameterTypes());
        final String compensationId = CompensationLocal.getInstance().getCompensationId();
        final TxTransaction txTransaction = method.getAnnotation(TxTransaction.class);
        final String transactionManager = txTransaction.transactionManager();
        final int waitMaxTime = txTransaction.waitMaxTime();
        final PropagationEnum propagation = txTransaction.propagation();
        TransactionInvocation invocation = new TransactionInvocation(clazz, thisMethod.getName(), args, method.getParameterTypes());
        TxTransactionInfo info = new TxTransactionInfo(invocation, transactionGroupId, compensationId, waitMaxTime, propagation, transactionManager);
        final Class css = txTransactionFactoryService.factoryOf(info);
        final TxTransactionHandler txTransactionHandler =
                (TxTransactionHandler) SpringBeanUtils.getInstance().getBean(css);
        return txTransactionHandler.handler(point, info);
    }
}
