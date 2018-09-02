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

package com.raincat.core.service.handler;

import com.raincat.common.bean.TxTransactionInfo;
import com.raincat.common.constant.CommonConstant;
import com.raincat.common.enums.PropagationEnum;
import com.raincat.common.enums.TransactionRoleEnum;
import com.raincat.common.enums.TransactionStatusEnum;
import com.raincat.common.exception.TransactionRuntimeException;
import com.raincat.common.holder.DateUtils;
import com.raincat.common.holder.IdWorkerUtils;
import com.raincat.common.holder.LogUtil;
import com.raincat.common.netty.bean.TxTransactionGroup;
import com.raincat.common.netty.bean.TxTransactionItem;
import com.raincat.core.compensation.manager.TxCompensationManager;
import com.raincat.core.concurrent.threadlocal.TxTransactionLocal;
import com.raincat.core.service.TxManagerMessageService;
import com.raincat.core.service.TxTransactionHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * this is tx transaction starter .
 *
 * @author xiaoyu
 */
@Component
public class StartTxTransactionHandler implements TxTransactionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartTxTransactionHandler.class);

    private final TxManagerMessageService txManagerMessageService;

    private final TxCompensationManager txCompensationManager;

    private final PlatformTransactionManager platformTransactionManager;

    @Autowired(required = false)
    public StartTxTransactionHandler(final TxManagerMessageService txManagerMessageService,
                                     final TxCompensationManager txCompensationManager,
                                     final PlatformTransactionManager platformTransactionManager) {
        this.txManagerMessageService = txManagerMessageService;
        this.txCompensationManager = txCompensationManager;
        this.platformTransactionManager = platformTransactionManager;
    }

    @Override
    public Object handler(final ProceedingJoinPoint point, final TxTransactionInfo info) throws Throwable {
        LogUtil.info(LOGGER, "tx-transaction start, class：{}", () -> point.getTarget().getClass());

        final String groupId = IdWorkerUtils.getInstance().createGroupId();

        //设置事务组ID
        TxTransactionLocal.getInstance().setTxGroupId(groupId);

        final String waitKey = IdWorkerUtils.getInstance().createTaskKey();

        String commitStatus = CommonConstant.TX_TRANSACTION_COMMIT_STATUS_BAD;

        //创建事务组信息
        final Boolean success = txManagerMessageService.saveTxTransactionGroup(newTxTransactionGroup(groupId, waitKey, info));
        if (success) {
            //如果发起方没有事务
            if (info.getPropagationEnum().getValue() == PropagationEnum.PROPAGATION_NEVER.getValue()) {
                try {
                    final Object res = point.proceed();
                    final Boolean commit = txManagerMessageService.preCommitTxTransaction(groupId);
                    if (commit) {
                        //通知tm完成事务
                        CompletableFuture.runAsync(() ->
                                txManagerMessageService
                                        .asyncCompleteCommit(groupId, waitKey,
                                                TransactionStatusEnum.COMMIT.getCode(), res));
                    }
                    return res;
                } catch (Throwable throwable) {
                    //通知tm整个事务组失败，需要回滚，（回滚那些正常提交的模块，他们正在等待通知。。。。）
                    txManagerMessageService.rollBackTxTransaction(groupId);
                    throwable.printStackTrace();
                    throw throwable;
                }
            }
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionStatus transactionStatus = platformTransactionManager.getTransaction(def);
            try {
                //发起调用
                final Object res = point.proceed();
                //保存本地补偿数据
                String compensateId = txCompensationManager.saveTxCompensation(info.getInvocation(), groupId, waitKey);
                final Boolean commit = txManagerMessageService.preCommitTxTransaction(groupId);
                if (commit) {
                    //我觉得到这一步了，应该是切面走完，然后需要提交了，此时应该都是进行提交的
                    //提交事务
                    platformTransactionManager.commit(transactionStatus);

                    LOGGER.info("发起者提交本地事务,补偿Id:[{}]", compensateId);


                    //通知tm完成事务
                    CompletableFuture.runAsync(() ->
                            txManagerMessageService
                                    .asyncCompleteCommit(groupId, waitKey,
                                            TransactionStatusEnum.COMMIT.getCode(), res));

                } else {
                    LogUtil.error(LOGGER, () -> "预提交失败!");
                    //这里建议不直接删除补偿信息，交由补偿任务控制，当前任务无法判定提交超时还是返回失败
                    //txCompensationManager.removeTxCompensation(compensateId);
                    platformTransactionManager.rollback(transactionStatus);
                }
                //删除补偿信息
                txCompensationManager.removeTxCompensation(compensateId);
                LogUtil.info(LOGGER, "tx-transaction end, class：{}", () -> point.getTarget().getClass());

                return res;

            } catch (final Throwable throwable) {
                //如果有异常 当前项目事务进行回滚 ，同时通知tm 整个事务失败
                platformTransactionManager.rollback(transactionStatus);
                //通知tm整个事务组失败，需要回滚，（回滚那些正常提交的模块，他们正在等待通知。。。。）
                txManagerMessageService.rollBackTxTransaction(groupId);
                //通知tm 自身事务回滚
                CompletableFuture.runAsync(() ->
                        txManagerMessageService
                                .asyncCompleteCommit(groupId, waitKey,
                                        TransactionStatusEnum.ROLLBACK.getCode(), null));

                throwable.printStackTrace();
                throw throwable;
            } finally {
                TxTransactionLocal.getInstance().removeTxGroupId();

                /**
                 *  1. 若事务提交成功这里不进行处理，此时completeFlag="0" ,则异常情况下进入补偿的任务认为当前任务还在处理中，不对其进行补偿处理;
                 *  2. 若事务未提交,当前任务更新completeFlag="1" ，补偿任务可以继续向下执行补偿
                 */
                /*if (CommonConstant.TX_TRANSACTION_COMMIT_STATUS_BAD.equals(commitStatus)) {
                    txCompensationManager.updateTxCompensation(groupId);
                }*/
            }
        } else {
            throw new TransactionRuntimeException("TxManager connection ex！");
        }
    }

    private TxTransactionGroup newTxTransactionGroup(final String groupId, final String taskKey, final TxTransactionInfo info) {
        //创建事务组信息
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        txTransactionGroup.setId(groupId);
        //tmManager 用redis hash 结构来存储 整个事务组的状态做为hash结构的第一条数据
        TxTransactionItem groupItem = new TxTransactionItem();
        //整个事务组状态为开始
        groupItem.setStatus(TransactionStatusEnum.BEGIN.getCode());
        //设置事务id为组的id  即为 hashKey
        groupItem.setTransId(groupId);
        groupItem.setTaskKey(groupId);
        groupItem.setCreateDate(DateUtils.getCurrentDateTime());
        //设置执行类名称
        groupItem.setTargetClass(info.getInvocation().getTargetClazz().getName());
        //设置执行类方法
        groupItem.setTargetMethod(info.getInvocation().getMethod());
        groupItem.setRole(TransactionRoleEnum.GROUP.getCode());
        List<TxTransactionItem> items = new ArrayList<>(2);
        items.add(groupItem);
        TxTransactionItem item = new TxTransactionItem();
        item.setTaskKey(taskKey);
        item.setTransId(IdWorkerUtils.getInstance().createUUID());
        item.setRole(TransactionRoleEnum.START.getCode());
        item.setStatus(TransactionStatusEnum.BEGIN.getCode());
        item.setTxGroupId(groupId);
        //设置事务最大等待时间
        item.setWaitMaxTime(info.getWaitMaxTime());
        //设置创建时间
        item.setCreateDate(DateUtils.getCurrentDateTime());
        //设置执行类名称
        item.setTargetClass(info.getInvocation().getTargetClazz().getName());
        //设置执行类方法
        item.setTargetMethod(info.getInvocation().getMethod());
        items.add(item);
        txTransactionGroup.setItemList(items);
        return txTransactionGroup;
    }

}
