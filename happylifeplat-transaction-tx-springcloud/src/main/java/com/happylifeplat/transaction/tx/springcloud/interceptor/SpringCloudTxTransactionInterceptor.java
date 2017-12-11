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
package com.happylifeplat.transaction.tx.springcloud.interceptor;

import com.happylifeplat.transaction.common.constant.CommonConstant;
import com.happylifeplat.transaction.core.concurrent.threadlocal.CompensationLocal;
import com.happylifeplat.transaction.core.interceptor.TxTransactionInterceptor;
import com.happylifeplat.transaction.core.service.AspectTransactionService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xiaoyu
 */
@Component
public class SpringCloudTxTransactionInterceptor implements TxTransactionInterceptor {

    private final AspectTransactionService aspectTransactionService;

    @Autowired
    public SpringCloudTxTransactionInterceptor(AspectTransactionService aspectTransactionService) {
        this.aspectTransactionService = aspectTransactionService;
    }


    @Override
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {
        final String compensationId = CompensationLocal.getInstance().getCompensationId();
        String groupId = null;
        if (StringUtils.isBlank(compensationId)) {
            //如果不是本地反射调用补偿
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = requestAttributes == null ? null : ((ServletRequestAttributes) requestAttributes).getRequest();
            groupId = request == null ? null : request.getHeader(CommonConstant.TX_TRANSACTION_GROUP);
        }

        return aspectTransactionService.invoke(groupId, pjp);
    }

}
