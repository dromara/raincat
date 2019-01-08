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

package org.dromara.raincat.core.concurrent.threadlocal;

/**
 * TxTransactionLocal.
 * @author xiaoyu
 */
public final class TxTransactionLocal {

    private static final ThreadLocal<String> CURRENT_LOCAL = new ThreadLocal<>();

    private static final TxTransactionLocal TX_TRANSACTION_LOCAL = new TxTransactionLocal();

    private TxTransactionLocal() {

    }

    public static TxTransactionLocal getInstance() {
        return TX_TRANSACTION_LOCAL;
    }

    public void setTxGroupId(final String txGroupId) {
        CURRENT_LOCAL.set(txGroupId);
    }

    public String getTxGroupId() {
        return CURRENT_LOCAL.get();
    }

    public void removeTxGroupId() {
        CURRENT_LOCAL.remove();
    }

}
