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
package com.happylifeplat.transaction.core.compensation.command;

import com.happylifeplat.transaction.common.enums.CompensationActionEnum;
import com.happylifeplat.transaction.common.bean.TransactionRecover;

import java.io.Serializable;

/**
 * @author xiaoyu
 */
public class TxCompensationAction implements Serializable {

    private static final long serialVersionUID = 7474184793963072848L;


    private CompensationActionEnum compensationActionEnum;


    private TransactionRecover transactionRecover;

    public CompensationActionEnum getCompensationActionEnum() {
        return compensationActionEnum;
    }

    public void setCompensationActionEnum(CompensationActionEnum compensationActionEnum) {
        this.compensationActionEnum = compensationActionEnum;
    }

    public TransactionRecover getTransactionRecover() {
        return transactionRecover;
    }

    public void setTransactionRecover(TransactionRecover transactionRecover) {
        this.transactionRecover = transactionRecover;
    }


}
