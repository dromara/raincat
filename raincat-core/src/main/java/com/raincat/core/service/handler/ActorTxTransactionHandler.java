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
import com.raincat.common.enums.NettyResultEnum;
import com.raincat.common.enums.TransactionRoleEnum;
import com.raincat.common.enums.TransactionStatusEnum;
import com.raincat.common.holder.DateUtils;
import com.raincat.common.holder.IdWorkerUtils;
import com.raincat.common.holder.LogUtil;
import com.raincat.common.netty.bean.TxTransactionItem;
import com.raincat.core.compensation.manager.TxCompensationManager;
import com.raincat.core.concurrent.task.BlockTask;
import com.raincat.core.concurrent.task.BlockTaskHelper;
import com.raincat.core.concurrent.threadlocal.TxTransactionLocal;
import com.raincat.core.concurrent.threadpool.TxTransactionThreadPool;
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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;

/**
 * this is tx transaction actor.
 *
 * @author xiaoyu
 */
@Component
public class ActorTxTransactionHandler implements TxTransactionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActorTxTransactionHandler.class);

    private final TxTransactionThreadPool txTransactionThreadPool;

    private final TxManagerMessageService txManagerMessageService;

    private final TxCompensationManager txCompensationManager;

    private final PlatformTransactionManager platformTransactionManager;

    @Autowired
    public ActorTxTransactionHandler(final TxCompensationManager txCompensationManager,
                                     final PlatformTransactionManager platformTransactionManager,
                                     final TxTransactionThreadPool txTransactionThreadPool,
                                     final TxManagerMessageService txManagerMessageService) {
        this.txCompensationManager = txCompensationManager;
        this.platformTransactionManager = platformTransactionManager;
        this.txTransactionThreadPool = txTransactionThreadPool;
        this.txManagerMessageService = txManagerMessageService;
    }

    @Override
    public Object handler(final ProceedingJoinPoint point, final TxTransactionInfo info) throws Throwable {
        LogUtil.info(LOGGER, "tx transaction actor begin.... groupId：{}", info::getTxGroupId);
        final String taskKey = IdWorkerUtils.getInstance().createTaskKey();
        final BlockTask task = BlockTaskHelper.getInstance().getTask(taskKey);
        TxTransactionLocal.getInstance().setTxGroupId(info.getTxGroupId());
        txTransactionThreadPool
                .newFixedThreadPool()
                .execute(() -> {
                    TxTransactionLocal.getInstance().setTxGroupId(info.getTxGroupId());
                    final String waitKey = IdWorkerUtils.getInstance().createTaskKey();

                    String commitStatus = CommonConstant.TX_TRANSACTION_COMMIT_STATUS_BAD;

                    final BlockTask waitTask = BlockTaskHelper.getInstance().getTask(waitKey);
                    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                    TransactionStatus transactionStatus = platformTransactionManager.getTransaction(def);
                      /*
                      1 设置返回数据res
                      2 唤醒主线程
                      3  向TxManager 发送提交整个事务组请求
                      4 自身进入阻塞，等待TxManager通知是否提交 超时会唤醒  我觉得这里应该是继续提交
                      5 TxManager 发送提交通知，唤醒线程， 进行提交
                      6 提交完成后，想txManager 发送事务已经完成的通知
                     */
                    try {
                        //添加事务组信息
                        final Boolean success = txManagerMessageService
                                .addTxTransaction(info.getTxGroupId(), buildItem(waitKey, info));
                        if (success) {
                            //发起调用
                            final Object res = point.proceed();
                            //设置返回数据，并唤醒之前等待的主线程
                            task.setAsyncCall(objects -> res);
                            task.signal();
                            //调用成功 保存本地补偿信息
                            String compensateId = txCompensationManager
                                    .saveTxCompensation(info.getInvocation(), info.getTxGroupId(), waitKey);
                            /*
                             *
                             *等待txManager通知（提交或者回滚） 此线程唤醒（txManager通知客户端，然后唤醒）
                             * 如果此时TxManager down机或者网络通信异常 需要再开一个调度线程来唤醒
                             */
                            final ScheduledFuture scheduledFuture = txTransactionThreadPool.multiScheduled(() -> {
                                LogUtil.info(LOGGER, "transaction group id：{}", info::getTxGroupId);
                                final BlockTask blockTask = BlockTaskHelper.getInstance().getTask(waitKey);
                                if (!blockTask.isNotify()) {
                                    //如果获取通知超时了，那么就去获取事务组的状态
                                    final int transactionGroupStatus = txManagerMessageService.findTransactionGroupStatus(info.getTxGroupId());
                                    if (TransactionStatusEnum.PRE_COMMIT.getCode() == transactionGroupStatus
                                            || TransactionStatusEnum.COMMIT.getCode() == transactionGroupStatus) {
                                        //如果事务组是预提交，或者是提交状态
                                        //表明事务组是成功的，这时候就算超时也应该去提交
                                        LogUtil.info(LOGGER, "transaction group id：{} when time out ,execute commit transaction!", info::getTxGroupId);
                                        waitTask.setAsyncCall(objects -> TransactionStatusEnum.COMMIT.getCode());
                                        waitTask.signal();

                                    } else {
                                        LogUtil.info(LOGGER, "transaction group id {}，time out ,execute rollback!", info::getTxGroupId);
                                        waitTask.setAsyncCall(objects -> NettyResultEnum.TIME_OUT.getCode());
                                        waitTask.signal();
                                    }
                                    return true;
                                } else {
                                    return false;
                                }

                            }, info.getWaitMaxTime());

                            waitTask.await();

                            LogUtil.info(LOGGER, "receive txManager command！{}", () -> info.getTxGroupId() + ":" + waitKey);

                            try {
                                //如果已经被唤醒，就不需要去执行调度线程了 ，关闭上面的调度线程池中的任务
                                if (!scheduledFuture.isDone()) {
                                    scheduledFuture.cancel(false);
                                }
                                final Integer status = (Integer) waitTask.getAsyncCall().callBack();
                                if (TransactionStatusEnum.COMMIT.getCode() == status) {
                                    //提交事务
                                    platformTransactionManager.commit(transactionStatus);

                                    //通知tm 自身事务提交
                                    asyncComplete(info.getTxGroupId(), waitKey, TransactionStatusEnum.COMMIT.getCode(), res);

                                } else {
                                    //回滚当前事务
                                    platformTransactionManager.rollback(transactionStatus);
                                    //通知tm 自身事务回滚
                                    asyncComplete(info.getTxGroupId(), waitKey, TransactionStatusEnum.ROLLBACK.getCode(), res);
                                }
                                //删除补偿信息
                                txCompensationManager.removeTxCompensation(compensateId);
                            } catch (Throwable throwable) {
                                platformTransactionManager.rollback(transactionStatus);
                                throwable.printStackTrace();
                            } finally {
                                BlockTaskHelper.getInstance().removeByKey(waitKey);
                               /* // 更新补偿信息
                                if (CommonConstant.TX_TRANSACTION_COMMIT_STATUS_BAD.equals(commitStatus)) {
                                    txCompensationManager.updateTxCompensation(compensateId);
                                }*/
                            }
                        } else {
                            platformTransactionManager.rollback(transactionStatus);
                        }

                    } catch (final Throwable throwable) {
                        throwable.printStackTrace();
                        //如果有异常 当前项目事务进行回滚
                        platformTransactionManager.rollback(transactionStatus);
                        //通知tm 自身事务失败
                        asyncComplete(info.getTxGroupId(),
                                waitKey, TransactionStatusEnum.FAILURE.getCode(), throwable.getMessage());

                        task.setAsyncCall(objects -> {
                            throw throwable;
                        });
                        task.signal();
                    }
                });

        task.await();
        LogUtil.info(LOGGER, () -> "actor tx-transaction-end");
        try {
            return task.getAsyncCall().callBack();
        } finally {
            BlockTaskHelper.getInstance().removeByKey(task.getKey());
        }
    }

    private void asyncComplete(final String txGroupId, final String waitKey, final Integer status, final Object res) {
        //通知tm 自身事务执行状态
        CompletableFuture.runAsync(() ->
                txManagerMessageService.asyncCompleteCommit(txGroupId, waitKey, status, res));
    }

    private TxTransactionItem buildItem(final String waitKey, final TxTransactionInfo info) {
        TxTransactionItem item = new TxTransactionItem();
        item.setTaskKey(waitKey);
        item.setTransId(IdWorkerUtils.getInstance().createUUID());
        //开始事务
        item.setStatus(TransactionStatusEnum.BEGIN.getCode());
        //设置为参与者角色
        item.setRole(TransactionRoleEnum.ACTOR.getCode());
        item.setTxGroupId(info.getTxGroupId());
        //设置事务最大等待时间
        item.setWaitMaxTime(info.getWaitMaxTime());
        //设置创建时间
        item.setCreateDate(DateUtils.getCurrentDateTime());
        //设置执行类名称
        item.setTargetClass(info.getInvocation().getTargetClazz().getName());
        //设置执行类方法
        item.setTargetMethod(info.getInvocation().getMethod());
        return item;
    }

}
