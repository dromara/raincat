package com.happylifeplat.transaction.tx.manager.service.execute;

import com.google.gson.Gson;
import com.happylifeplat.transaction.common.entity.TxManagerServer;
import com.happylifeplat.transaction.common.enums.NettyMessageActionEnum;
import com.happylifeplat.transaction.common.enums.TransactionStatusEnum;
import com.happylifeplat.transaction.common.holder.LogUtil;
import com.happylifeplat.transaction.common.holder.httpclient.OkHttpTools;
import com.happylifeplat.transaction.common.netty.bean.HeartBeat;
import com.happylifeplat.transaction.common.netty.bean.TxTransactionGroup;
import com.happylifeplat.transaction.common.netty.bean.TxTransactionItem;
import com.happylifeplat.transaction.tx.manager.config.ChannelSender;
import com.happylifeplat.transaction.tx.manager.config.Constant;
import com.happylifeplat.transaction.tx.manager.config.ExecutorMessageTool;
import com.happylifeplat.transaction.tx.manager.service.TxManagerService;
import com.happylifeplat.transaction.tx.manager.socket.SocketManager;
import io.netty.channel.Channel;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * TxTransactionExecutorService 实现类
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/26 11:16
 * @since JDK 1.8
 */
@Component
public class TxTransactionExecutorService extends AbstractTxTransactionExecutor {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TxTransactionExecutorService.class);


    private static final Gson gson = new Gson();


    @Autowired
    public TxTransactionExecutorService(TxManagerService txManagerService) {
        super.setTxManagerService(txManagerService);
    }


    /**
     * 执行回滚动作
     *
     * @param txGroupId          事务组id
     * @param txTransactionItems 连接到当前tm的channel信息
     * @param elseItems          连接到其他tm的channel信息
     */
    @Override
    protected void doRollBack(String txGroupId, List<TxTransactionItem> txTransactionItems, List<TxTransactionItem> elseItems) {
        try {
            if (CollectionUtils.isNotEmpty(txTransactionItems)) {
                final CompletableFuture[] cfs = txTransactionItems
                        .stream()
                        .map(item ->
                                CompletableFuture.runAsync(() -> {
                                    ChannelSender channelSender = new ChannelSender();
                                    HeartBeat heartBeat = ExecutorMessageTool.buildMessage(item, channelSender,
                                            TransactionStatusEnum.ROLLBACK);
                                    if (Objects.nonNull(channelSender.getChannel())) {
                                        channelSender.getChannel().writeAndFlush(heartBeat);
                                    } else {
                                        LOGGER.error("txManger rollback指令失败，channel为空，事务组id：{}, 事务taskId为:{}",
                                                txGroupId, item.getTaskKey());
                                    }

                                }).whenComplete((v, e) ->
                                        LogUtil.info(LOGGER, "txManger 成功发送rollback指令 事务taskId为：{}", item::getTaskKey)))
                        .toArray(CompletableFuture[]::new);
                CompletableFuture.allOf(cfs).join();
                LogUtil.info(LOGGER, "txManger 成功发送rollback指令 事务组id为：{}", () -> txGroupId);
            }
            httpExecute(elseItems, TransactionStatusEnum.ROLLBACK);

        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.info(LOGGER, "txManger 发送rollback指令异常 ", e::getMessage);
        }
    }


    /**
     * 执行提交动作
     *
     * @param txGroupId          事务组id
     * @param txTransactionItems 连接到当前tm的channel信息
     * @param elseItems          连接到其他tm的channel信息
     */
    @Override
    protected void doCommit(String txGroupId, List<TxTransactionItem> txTransactionItems, List<TxTransactionItem> elseItems) {
        try {
            txTransactionItems.forEach(item -> {
                ChannelSender sender = new ChannelSender();
                HeartBeat heartBeat = ExecutorMessageTool.buildMessage(item, sender, TransactionStatusEnum.COMMIT);
                if (Objects.nonNull(sender.getChannel())) {
                    sender.getChannel().writeAndFlush(heartBeat);
                    LogUtil.info(LOGGER, "txManger 成功发送doCommit指令 事务taskId为：{}", item::getTaskKey);
                } else {
                    LOGGER.error("txManger 发送doCommit指令失败，channel为空，事务组id：{}, 事务taskId为:{}", txGroupId, item.getTaskKey());
                }

            });

            httpExecute(elseItems, TransactionStatusEnum.COMMIT);

          /*  final CompletableFuture[] cfs = txTransactionItems
                    .stream()
                    .map(item ->
                            CompletableFuture.runAsync(() -> {
                                HeartBeat heartBeat = buildCommitMessage(txGroupId, item);
                                item.getChannel().writeAndFlush(heartBeat);
                            }).whenComplete((v, e) ->
                                    LogUtil.info(LOGGER, "txManger 成功发送doCommit指令 事务taskId为：{}", item::getTaskKey)))
                    .toArray(CompletableFuture[]::new);
            // CompletableFuture.allOf(cfs).join();
            LogUtil.info(LOGGER, "txManger 成功发送doCommit指令 事务组id为：{}", () -> txGroupId);*/
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.info(LOGGER, "txManger 发送doCommit指令异常 ", e::getMessage);
        }

    }


    /**
     * 获取当前连接的channel  为什么？
     * 因为如果tm是集群环境，可能业务的channel对象连接到不同的tm
     * 那么当前的tm可没有其他业务模块的长连接信息，那么就应该做：
     * 1.检查当前tm的channel状态，并只提交当前渠道的命令
     * 2.通知 连接到其他tm的channel，执行命令
     * 通过http 执行 连接到其他tm 的channel
     *
     * @param elseItems             其他的渠道集合
     * @param transactionStatusEnum 执行动作
     */
    private void httpExecute(List<TxTransactionItem> elseItems, TransactionStatusEnum transactionStatusEnum) {
        if (CollectionUtils.isNotEmpty(elseItems)) {
            //按照域名进行分组
            final Map<String, List<TxTransactionItem>> senderItems =
                    elseItems.stream().collect(Collectors.groupingBy(TxTransactionItem::getTmDomain));
            senderItems.forEach((k, v) -> {
                try {
                    if (transactionStatusEnum.getCode() == TransactionStatusEnum.COMMIT.getCode()) {
                        OkHttpTools.getInstance().post(String.format(Constant.httpCommit, k), gson.toJson(v));
                    } else if (transactionStatusEnum.getCode() == TransactionStatusEnum.ROLLBACK.getCode()) {
                        OkHttpTools.getInstance().post(String.format(Constant.httpRollback, k), gson.toJson(v));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        }
    }


}
