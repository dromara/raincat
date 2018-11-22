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

package org.dromara.raincat.manager.service.execute;

import org.apache.commons.collections.CollectionUtils;
import org.dromara.raincat.common.enums.TransactionStatusEnum;
import org.dromara.raincat.common.holder.LogUtil;
import org.dromara.raincat.common.netty.bean.HeartBeat;
import org.dromara.raincat.common.netty.bean.TxTransactionItem;
import org.dromara.raincat.manager.config.ChannelSender;
import org.dromara.raincat.manager.config.ExecutorMessageTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * HttpTransactionExecutor.
 * @author xiaoyu
 */
@Component
public class HttpTransactionExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpTransactionExecutor.class);

    public void rollBack(final List<TxTransactionItem> txTransactionItems) {
        try {
            execute(txTransactionItems, TransactionStatusEnum.ROLLBACK);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.info(LOGGER, "txManger 发送rollback指令异常 ", e::getMessage);
        }
    }

    public void commit(final List<TxTransactionItem> txTransactionItems) {
        try {
            execute(txTransactionItems, TransactionStatusEnum.COMMIT);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.info(LOGGER, "txManger 发送commit 指令异常 ", e::getMessage);
        }
    }

    private void execute(final List<TxTransactionItem> txTransactionItems,
                         final TransactionStatusEnum transactionStatusEnum) {
        if (CollectionUtils.isNotEmpty(txTransactionItems)) {
            final CompletableFuture[] cfs = txTransactionItems
                    .stream()
                    .map(item ->
                            CompletableFuture.runAsync(() -> {
                                ChannelSender channelSender = new ChannelSender();
                                final HeartBeat heartBeat = ExecutorMessageTool.buildMessage(item,
                                        channelSender, transactionStatusEnum);
                                if (Objects.nonNull(channelSender.getChannel())) {
                                    channelSender.getChannel().writeAndFlush(heartBeat);
                                } else {
                                    LOGGER.error("txMange {},指令失败，channel为空，事务组id：{}, 事务taskId为:{}",
                                            transactionStatusEnum.getDesc(), item.getTxGroupId(), item.getTaskKey());
                                }

                            }).whenComplete((v, e) ->
                                    LOGGER.info("txManger 成功发送 {} 指令 事务taskId为：{}", transactionStatusEnum.getDesc(), item.getTaskKey())))
                    .toArray(CompletableFuture[]::new);
            CompletableFuture.allOf(cfs).join();
        }
    }

}
