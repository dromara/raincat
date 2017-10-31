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

import com.happylifeplat.transaction.common.netty.bean.TxTransactionGroup;
import com.happylifeplat.transaction.common.netty.bean.TxTransactionItem;

/**
 * @author xiaoyu
 */
public interface TxManagerMessageService {

    /**
     * 保存事务组 在事务发起方的时候进行调用
     *
     * @param txTransactionGroup 事务组
     * @return true 成功 false 失败
     */
    Boolean saveTxTransactionGroup(TxTransactionGroup txTransactionGroup);


    /**
     * 往事务组添加事务
     *
     * @param txGroupId         事务组id
     * @param txTransactionItem 子事务项
     * @return true 成功 false 失败
     */
    Boolean addTxTransaction(String txGroupId, TxTransactionItem txTransactionItem);


    /**
     * 获取事务组状态
     *
     * @param txGroupId 事务组id
     * @return 事务组状态
     */
    int findTransactionGroupStatus(String txGroupId);


    /**
     * 获取事务组信息
     *
     * @param txGroupId 事务组id
     * @return TxTransactionGroup
     */
    TxTransactionGroup findByTxGroupId(String txGroupId);


    /**
     * 通知tm 回滚整个事务组
     *
     * @param txGroupId 事务组id
     * @return true 成功 false 失败
     */
    Boolean rollBackTxTransaction(String txGroupId);


    /**
     * 通知tm自身业务已经执行完成，等待提交事务
     * tm 收到后，进行pre_commit  再进行doCommit
     *
     * @param txGroupId 事务组id
     * @return true 成功 false 失败
     */
    Boolean preCommitTxTransaction(String txGroupId);


    /**
     * 完成提交自身的事务
     *
     * @param txGroupId 事务组id
     * @param taskKey   子事务的taskKey
     * @param status    状态  {@linkplain com.happylifeplat.transaction.common.enums.TransactionStatusEnum}
     * @return true 成功 false 失败
     */
    Boolean completeCommitTxTransaction(String txGroupId, String taskKey, int status);


    /**
     * 异步完成自身的提交
     *
     * @param txGroupId 事务组id
     * @param taskKey   子事务的taskKey
     * @param status    状态  {@linkplain com.happylifeplat.transaction.common.enums.TransactionStatusEnum}
     * @param message   完成信息 返回结果，或者是异常信息
     */
    void asyncCompleteCommit(String txGroupId, String taskKey, int status,Object message);

    /**
     * 提交参与者事务状态
     *
     * @param txGroupId         事务组id
     * @param txTransactionItem 参与者
     * @param status            状态
     * @return
     */
    Boolean commitActorTxTransaction(String txGroupId, TxTransactionItem txTransactionItem, int status);

}
