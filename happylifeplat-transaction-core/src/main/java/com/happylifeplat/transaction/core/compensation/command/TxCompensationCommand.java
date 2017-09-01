package com.happylifeplat.transaction.core.compensation.command;

import com.happylifeplat.transaction.common.enums.CompensationActionEnum;
import com.happylifeplat.transaction.common.enums.TransactionStatusEnum;
import com.happylifeplat.transaction.common.holder.IdWorkerUtils;
import com.happylifeplat.transaction.common.holder.LogUtil;
import com.happylifeplat.transaction.core.bean.TransactionInvocation;
import com.happylifeplat.transaction.core.bean.TransactionRecover;
import com.happylifeplat.transaction.core.compensation.TxCompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/19 16:01
 * @since JDK 1.8
 */
@Service
public class TxCompensationCommand implements Command {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TxCompensationCommand.class);

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
      /*  CompletableFuture.supplyAsync(() -> txCompensationService.submit(txCompensationAction))
                .thenAccept(result -> {
                    if (result) {
                        LogUtil.info(LOGGER, "补偿操作提交成功！,执行的操作为:{}",
                                () -> txCompensationAction.getCompensationActionEnum().getCode());
                    }
                });*/

    }


    public String saveTxCompensation(TransactionInvocation invocation, String groupId, String taskId) {
        TxCompensationAction action = new TxCompensationAction();
        action.setCompensationActionEnum(CompensationActionEnum.SAVE);
        TransactionRecover recover = new TransactionRecover();
        recover.setRetriedCount(1);
        recover.setStatus(TransactionStatusEnum.BEGIN.getCode());
        recover.setId(IdWorkerUtils.getInstance().createGroupId());
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
