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

package com.raincat.core.compensation.manager;

import com.raincat.common.bean.TransactionInvocation;
import com.raincat.common.bean.TransactionRecover;
import com.raincat.common.constant.CommonConstant;
import com.raincat.common.enums.CompensationActionEnum;
import com.raincat.common.enums.CompensationOperationTypeEnum;
import com.raincat.common.enums.TransactionStatusEnum;
import com.raincat.common.holder.IdWorkerUtils;
import com.raincat.core.disruptor.publisher.TxTransactionEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * TxCompensationManager.
 *
 * @author xiaoyu
 */
@Service
public class TxCompensationManager {

    private final TxTransactionEventPublisher txTransactionEventPublisher;

    @Autowired
    public TxCompensationManager(final TxTransactionEventPublisher txTransactionEventPublisher) {
        this.txTransactionEventPublisher = txTransactionEventPublisher;
    }

    /**
     * save TransactionRecover data.
     *
     * @param invocation {@linkplain TransactionInvocation}
     * @param groupId    this is transaction groupId
     * @param taskId     taskId
     * @return groupId.
     */
    public String saveTxCompensation(final TransactionInvocation invocation, final String groupId, final String taskId) {
        TransactionRecover recover = new TransactionRecover();
        recover.setRetriedCount(1);
        recover.setStatus(TransactionStatusEnum.BEGIN.getCode());
        recover.setId(String.valueOf(IdWorkerUtils.getInstance().randomUUID()));
        recover.setTransactionInvocation(invocation);
        recover.setGroupId(groupId);
        recover.setTaskId(taskId);
        recover.setCreateTime(new Date());
        recover.setCompleteFlag(CommonConstant.TX_TRANSACTION_COMPLETE_FLAG_BAD);
        txTransactionEventPublisher.publishEvent(recover, CompensationActionEnum.SAVE.getCode());
        return recover.getId();
    }

    /**
     * delete TransactionRecover.
     *
     * @param id transaction groupId.
     */
    public void removeTxCompensation(final String id) {
        TransactionRecover recover = new TransactionRecover();
        recover.setId(id);
        txTransactionEventPublisher.publishEvent(recover, CompensationActionEnum.DELETE.getCode());
    }

    /**
     * update TransactionRecover.
     *
     * @param id
     */
    public void updateTxCompensation(final String id) {
        TransactionRecover recover = new TransactionRecover();
        recover.setId(id);
        recover.setCompleteFlag(CommonConstant.TX_TRANSACTION_COMPLETE_FLAG_OK);
        recover.setOperation(CompensationOperationTypeEnum.TASK_EXECUTE.getCode());
        txTransactionEventPublisher.publishEvent(recover, CompensationActionEnum.UPDATE.getCode());
    }
}
