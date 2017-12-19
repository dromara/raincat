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
package com.happylifeplat.transaction.tx.motan.interceptor;


import com.happylifeplat.transaction.common.constant.CommonConstant;
import com.happylifeplat.transaction.core.interceptor.TxTransactionInterceptor;
import com.happylifeplat.transaction.core.service.AspectTransactionService;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.RpcContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * @author xiaoyu
 */
@Component
public class MotanTxTransactionInterceptor implements TxTransactionInterceptor {

    private final AspectTransactionService aspectTransactionService;

    @Autowired
    public MotanTxTransactionInterceptor(AspectTransactionService aspectTransactionService) {
        this.aspectTransactionService = aspectTransactionService;
    }


    @Override
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {

        String groupId = "";
        final Request request = RpcContext.getContext().getRequest();
        if (Objects.nonNull(request)) {
            final Map<String, String> attachments = request.getAttachments();
            if (attachments != null && !attachments.isEmpty()) {
                groupId = attachments.get(CommonConstant.TX_TRANSACTION_GROUP);
            }
        }
        return aspectTransactionService.invoke(groupId, pjp);
    }

}
