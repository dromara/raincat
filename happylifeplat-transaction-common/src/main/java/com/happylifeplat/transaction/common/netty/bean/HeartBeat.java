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

import java.io.Serializable;

/**
 * @author xiaoyu
 */
public class HeartBeat implements Serializable {

    private static final long serialVersionUID = 4183978848464761529L;


    /**
     * 执行动作 {@linkplain com.happylifeplat.transaction.common.enums.NettyMessageActionEnum}
     */
    private int action;


    /**
     * 执行发送数据任务task key
     */
    private String key;


    /**
     * {@linkplain com.happylifeplat.transaction.common.enums.NettyResultEnum}
     */
    private int result;


    /**
     * 事务组信息
     */
    private TxTransactionGroup txTransactionGroup;

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public TxTransactionGroup getTxTransactionGroup() {
        return txTransactionGroup;
    }

    public void setTxTransactionGroup(TxTransactionGroup txTransactionGroup) {
        this.txTransactionGroup = txTransactionGroup;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
