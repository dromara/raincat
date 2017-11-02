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
import com.happylifeplat.transaction.common.enums.TransactionStatusEnum;
import com.happylifeplat.transaction.common.holder.IdWorkerUtils;
import com.happylifeplat.transaction.common.bean.TransactionInvocation;
import com.happylifeplat.transaction.common.bean.TransactionRecover;
import com.happylifeplat.transaction.core.compensation.TxCompensationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author xiaoyu
 */
@Service
public class TxCompensationCommand implements Command {

    private final TxCompensationService txCompensationService;

    @Autowired
    public TxCompensationCommand(TxCompensationService txCompensationService) {
        this.txCompensationService = txCompensationService;
    }

    /**
     * 执行命令接口
     *
     * @param txCompensationAction 封装命令信息
     */
    @Override
    public void execute(TxCompensationAction txCompensationAction) {
        txCompensationService.submit(txCompensationAction);
    }


    public String saveTxCompensation(TransactionInvocation invocation, String groupId, String taskId) {
        TxCompensationAction action = new TxCompensationAction();
        action.setCompensationActionEnum(CompensationActionEnum.SAVE);
        TransactionRecover recover = new TransactionRecover();
        recover.setRetriedCount(1);
        recover.setStatus(TransactionStatusEnum.BEGIN.getCode());
        recover.setId(groupId);
        recover.setTransactionInvocation(invocation);
        recover.setGroupId(groupId);
        recover.setTaskId(taskId);
        recover.setCreateTime(new Date());
        action.setTransactionRecover(recover);
        execute(action);
        return recover.getId();
    }

    public void removeTxCompensation(String compensateId) {
        TxCompensationAction action = new TxCompensationAction();
        action.setCompensationActionEnum(CompensationActionEnum.DELETE);
        TransactionRecover recover = new TransactionRecover();
        recover.setId(compensateId);
        action.setTransactionRecover(recover);
        execute(action);
    }

}
