package com.happylifeplat.transaction.core.service.handler;

import com.happylifeplat.transaction.common.enums.NettyResultEnum;
import com.happylifeplat.transaction.common.enums.TransactionRoleEnum;
import com.happylifeplat.transaction.common.enums.TransactionStatusEnum;
import com.happylifeplat.transaction.common.holder.IdWorkerUtils;
import com.happylifeplat.transaction.common.holder.LogUtil;
import com.happylifeplat.transaction.common.netty.bean.TxTransactionItem;
import com.happylifeplat.transaction.core.bean.TxTransactionInfo;
import com.happylifeplat.transaction.core.compensation.command.TxCompensationCommand;
import com.happylifeplat.transaction.core.concurrent.task.AsyncCall;
import com.happylifeplat.transaction.core.concurrent.task.BlockTask;
import com.happylifeplat.transaction.core.concurrent.task.BlockTaskHelper;
import com.happylifeplat.transaction.core.concurrent.threadlocal.TxTransactionLocal;
import com.happylifeplat.transaction.core.concurrent.threadpool.TransactionThreadPool;
import com.happylifeplat.transaction.core.service.TxManagerMessageService;
import com.happylifeplat.transaction.core.service.TxTransactionHandler;
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
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * 分布式事务运参与者
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/20 16:02
 * @since JDK 1.8
 */

@Component
public class ActorTxTransactionHandler implements TxTransactionHandler {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ActorTxTransactionHandler.class);

    private final TransactionThreadPool transactionThreadPool;
    private final TxManagerMessageService txManagerMessageService;

    private final TxCompensationCommand txCompensationCommand;

    private final PlatformTransactionManager platformTransactionManager;

    @Autowired
    public ActorTxTransactionHandler(TxCompensationCommand txCompensationCommand, PlatformTransactionManager platformTransactionManager, TransactionThreadPool transactionThreadPool, TxManagerMessageService txManagerMessageService) {
        this.txCompensationCommand = txCompensationCommand;
        this.platformTransactionManager = platformTransactionManager;
        this.transactionThreadPool = transactionThreadPool;
        this.txManagerMessageService = txManagerMessageService;
    }


    @Override
    public Object handler(ProceedingJoinPoint point, TxTransactionInfo info) throws Throwable {

        LogUtil.info(LOGGER, "分布式事务参与方，开始执行,事务组id：{}", info::getTxGroupId);
        final String taskKey = IdWorkerUtils.getInstance().createTaskKey();
        final BlockTask task = BlockTaskHelper.getInstance().getTask(taskKey);

        transactionThreadPool
                .newFixedThreadPool()
                .execute(() -> {
                    final String waitKey = IdWorkerUtils.getInstance().createTaskKey();
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
                        TxTransactionItem item = new TxTransactionItem();
                        item.setTaskKey(waitKey);
                        item.setTransId(IdWorkerUtils.getInstance().createUUID());
                        item.setStatus(TransactionStatusEnum.BEGIN.getCode());//开始事务
                        item.setRole(TransactionRoleEnum.ACTOR.getCode());//参与者
                        item.setTxGroupId(info.getTxGroupId());

                        //添加事务组信息
                        final Boolean success = txManagerMessageService.addTxTransaction(info.getTxGroupId(), item);
                        if (success) {
                            //发起调用
                            final Object res = point.proceed();

                            //设置返回数据，并唤醒之前等待的主线程
                            task.setAsyncCall(objects -> res);
                            task.signal();

                            //调用成功 保存本地补偿信息
                            String compensateId = txCompensationCommand.saveTxCompensation(info.getInvocation(),
                                    info.getTxGroupId(), waitKey);


                            /*
                             *
                             *等待txManager通知（提交或者回滚） 此线程唤醒（txManager通知客户端，然后唤醒）
                             * 如果此时TxManager down机或者网络通信异常 需要再开一个调度线程来唤醒
                             */
                            final ScheduledFuture scheduledFuture = transactionThreadPool.multiThreadscheduled(() -> {
                                LogUtil.info(LOGGER, "事务组id：{}，自动超时处理", info::getTxGroupId);
                                final BlockTask blockTask = BlockTaskHelper.getInstance().getTask(waitKey);
                                if (!blockTask.isNotify()) {
                                    //如果获取通知超时了，那么就去获取事务组的状态
                                    final int transactionGroupStatus = txManagerMessageService.findTransactionGroupStatus(info.getTxGroupId());
                                    if (TransactionStatusEnum.PRE_COMMIT.getCode() == transactionGroupStatus ||
                                            TransactionStatusEnum.COMMIT.getCode() == transactionGroupStatus) {
                                        //如果事务组是预提交，或者是提交状态
                                        //表明事务组是成功的，这时候就算超时也应该去提交
                                        LogUtil.info(LOGGER, "事务组id：{}，自动超时，获取事务组状态为提交，进行提交!", info::getTxGroupId);
                                        waitTask.setAsyncCall(objects -> TransactionStatusEnum.COMMIT.getCode());
                                        waitTask.signal();

                                    } else {
                                        LogUtil.info(LOGGER, "事务组id：{}，自动超时进行回滚!", info::getTxGroupId);
                                        waitTask.setAsyncCall(objects -> NettyResultEnum.TIME_OUT.getCode());
                                        waitTask.signal();
                                    }
                                    LOGGER.error("============通过定时任务来唤醒线程！事务状态为:{}", transactionGroupStatus);
                                    return true;
                                } else {
                                    return false;
                                }

                            });
                            try {
                                waitTask.await();
                                //如果已经被唤醒，就不需要去执行调度线程了 ，关闭上面的调度线程池中的任务
                                if (!scheduledFuture.isDone()) {
                                    scheduledFuture.cancel(false);
                                }
                                final Integer status = (Integer) waitTask.getAsyncCall().callBack();
                                if (TransactionStatusEnum.COMMIT.getCode() == status) {

                                    //提交事务
                                    platformTransactionManager.commit(transactionStatus);

                                    //删除本地补偿信息
                                    txCompensationCommand.removeTxCompensation(compensateId);

                                    //通知tm 自身事务已经完成

                                    //通知tm完成事务
                                    CompletableFuture.runAsync(() ->
                                            txManagerMessageService
                                                    .AsyncCompleteCommitTxTransaction(info.getTxGroupId(), waitKey,
                                                            TransactionStatusEnum.COMMIT.getCode()));
                                } else if (NettyResultEnum.TIME_OUT.getCode() == status) {

                                    //如果超时了，就回滚当前事务
                                    platformTransactionManager.rollback(transactionStatus);

                                    //通知tm 自身事务需要回滚,不能提交
                                    CompletableFuture.runAsync(() ->
                                            txManagerMessageService
                                                    .AsyncCompleteCommitTxTransaction(info.getTxGroupId(), waitKey,
                                                            TransactionStatusEnum.ROLLBACK.getCode()));
                                }
                            } catch (Throwable throwable) {
                                platformTransactionManager.rollback(transactionStatus);
                                throwable.printStackTrace();
                            } finally {
                                BlockTaskHelper.getInstance().removeByKey(waitKey);
                            }

                        } else {
                            platformTransactionManager.rollback(transactionStatus);
                        }

                    } catch (final Throwable throwable) {
                        throwable.printStackTrace();
                        //如果有异常 当前项目事务进行回滚 ，同时通知tm 整个事务失败
                        platformTransactionManager.rollback(transactionStatus);

                        task.setAsyncCall(objects -> {
                            throw throwable;
                        });
                        task.signal();

                    }

                });

        task.await();
        LogUtil.info(LOGGER, "actor tx-transaction-end:{}", () -> "参与分布式模块执行完毕！");

        try {
            return task.getAsyncCall().callBack();
        } finally {
            BlockTaskHelper.getInstance().removeByKey(task.getKey());
        }


    }


}
