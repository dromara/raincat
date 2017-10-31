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
package com.happylifeplat.transaction.common.netty.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * @author xiaoyu
 */
@Data
public class TxTransactionGroup implements Serializable {


    private static final long serialVersionUID = -8826219545126676832L;

    /**
     * 事务组id
     */
    private String id;

    /**
     * 事务等待时间
     */
    private int waitTime;

    /**
     * 事务状态 {@linkplain com.happylifeplat.transaction.common.enums.TransactionStatusEnum}
     */
    private int status;

    private  List<TxTransactionItem> itemList;

}
