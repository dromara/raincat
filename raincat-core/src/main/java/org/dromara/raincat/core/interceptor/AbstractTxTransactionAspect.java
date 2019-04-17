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

package org.dromara.raincat.core.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * AbstractTxTransactionAspect.
 *
 * @author xiaoyu
 */
@Aspect
public abstract class AbstractTxTransactionAspect {

    private TxTransactionInterceptor txTransactionInterceptor;

    /**
     * Sets tx transaction interceptor.
     *
     * @param txTransactionInterceptor the tx transaction interceptor
     */
    public void setTxTransactionInterceptor(final TxTransactionInterceptor txTransactionInterceptor) {
        this.txTransactionInterceptor = txTransactionInterceptor;
    }

    /**
     * Tx transaction interceptor.
     */
    @Pointcut("@annotation(org.dromara.raincat.annotation.TxTransaction)")
    public void txTransactionInterceptor() {

    }

    /**
     * Intercept tx transaction object.
     *
     * @param proceedingJoinPoint the proceeding join point
     * @return the object
     * @throws Throwable the throwable
     */
    @Around("txTransactionInterceptor()")
    public Object interceptTxTransaction(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return txTransactionInterceptor.interceptor(proceedingJoinPoint);
    }

    /**
     * spring bean order.
     *
     * @return order order
     */
    public abstract int getOrder();
}
