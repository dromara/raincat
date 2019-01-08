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

package org.dromara.raincat.manager.config;

import org.dromara.raincat.common.enums.NettyMessageActionEnum;
import org.dromara.raincat.common.enums.TransactionStatusEnum;
import org.dromara.raincat.common.netty.bean.HeartBeat;
import org.dromara.raincat.common.netty.bean.TxTransactionGroup;
import org.dromara.raincat.common.netty.bean.TxTransactionItem;
import org.dromara.raincat.manager.socket.SocketManager;
import io.netty.channel.Channel;

import java.util.Collections;
import java.util.Objects;

/**
 * ExecutorMessageTool.
 * @author xiaoyu
 */
public class ExecutorMessageTool {

    public static HeartBeat buildMessage(final TxTransactionItem item,
                                         final ChannelSender channelSender,
                                         final TransactionStatusEnum transactionStatusEnum) {
        HeartBeat heartBeat = new HeartBeat();
        Channel channel = SocketManager.getInstance().getChannelByModelName(item.getModelName());
        if (Objects.nonNull(channel)) {
            if (channel.isActive()) {
                channelSender.setChannel(channel);
            }
        }
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        if (TransactionStatusEnum.ROLLBACK.getCode() == transactionStatusEnum.getCode()) {
            heartBeat.setAction(NettyMessageActionEnum.ROLLBACK.getCode());
            item.setStatus(TransactionStatusEnum.ROLLBACK.getCode());
            txTransactionGroup.setStatus(TransactionStatusEnum.ROLLBACK.getCode());
        } else if (TransactionStatusEnum.COMMIT.getCode() == transactionStatusEnum.getCode()) {
            heartBeat.setAction(NettyMessageActionEnum.COMPLETE_COMMIT.getCode());
            item.setStatus(TransactionStatusEnum.COMMIT.getCode());
            txTransactionGroup.setStatus(TransactionStatusEnum.COMMIT.getCode());
        }
        txTransactionGroup.setItemList(Collections.singletonList(item));
        heartBeat.setTxTransactionGroup(txTransactionGroup);
        return heartBeat;
    }
}
