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

package org.dromara.raincat.core.helper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * The type Transaction manager helper.
 *
 * @author xiaoyu
 */
public class TransactionManagerHelper {

    /**
     * description:
     *
     * @param transactionManager the name of transactionManager
     * @return if transactionManager is blank return the primary PlatformTransactionManager
     * otherwise return the PlatformTransactionManager by the name.
     */
    public static PlatformTransactionManager getTransactionManager(String transactionManager) {
        if (StringUtils.isBlank(transactionManager)) {
            return SpringBeanUtils.getInstance().getBean(PlatformTransactionManager.class);
        } else {
            return SpringBeanUtils.getInstance().getBean(transactionManager, PlatformTransactionManager.class);
        }
    }

}
