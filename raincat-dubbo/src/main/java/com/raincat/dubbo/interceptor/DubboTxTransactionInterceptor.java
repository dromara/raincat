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

package com.raincat.dubbo.interceptor;

import com.alibaba.dubbo.rpc.RpcContext;
import com.raincat.common.constant.CommonConstant;
import com.raincat.core.interceptor.TxTransactionInterceptor;
import com.raincat.core.service.AspectTransactionService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * DubboTxTransactionInterceptor.
 * @author xiaoyu
 */
@Component
public class DubboTxTransactionInterceptor implements TxTransactionInterceptor {

    private final AspectTransactionService aspectTransactionService;

    @Autowired
    public DubboTxTransactionInterceptor(final AspectTransactionService aspectTransactionService) {
        this.aspectTransactionService = aspectTransactionService;
    }

    @Override
    public Object interceptor(final ProceedingJoinPoint pjp) throws Throwable {
        String groupId = RpcContext.getContext().getAttachment(CommonConstant.TX_TRANSACTION_GROUP);
        return aspectTransactionService.invoke(groupId, pjp);
    }

}
