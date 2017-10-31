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
package com.happylifeplat.transaction.core.service;

import com.happylifeplat.transaction.common.bean.TxTransactionInfo;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author xiaoyu
 */
@FunctionalInterface
public interface TxTransactionHandler {

    /**
     * 分布式事务处理接口
     *
     * @param point point 切点
     * @param info  信息
     * @return Object
     * @throws Throwable 异常
     */
    Object handler(ProceedingJoinPoint point, TxTransactionInfo info) throws Throwable;
}
