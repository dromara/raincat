package com.happylifeplat.transaction.tx.manager.config;

import com.happylifeplat.transaction.common.enums.NettyMessageActionEnum;
import com.happylifeplat.transaction.common.enums.TransactionStatusEnum;
import com.happylifeplat.transaction.common.netty.bean.HeartBeat;
import com.happylifeplat.transaction.common.netty.bean.TxTransactionGroup;
import com.happylifeplat.transaction.common.netty.bean.TxTransactionItem;
import com.happylifeplat.transaction.tx.manager.socket.SocketManager;
import io.netty.channel.Channel;

import java.util.Collections;
import java.util.Objects;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/8/8 18:04
 * @since JDK 1.8
 */
public class ExecutorMessageTool {


     public  static HeartBeat buildMessage(TxTransactionItem item, ChannelSender channelSender, TransactionStatusEnum transactionStatusEnum) {
        HeartBeat heartBeat = new HeartBeat();
        Channel channel = SocketManager.getInstance().getChannelByModelName(item.getModelName());
        if (Objects.nonNull(channel)) {
            if (channel.isActive()) {
                channelSender.setChannel(channel);
            }
        }
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        if(TransactionStatusEnum.ROLLBACK.getCode()==transactionStatusEnum.getCode()){
            heartBeat.setAction(NettyMessageActionEnum.ROLLBACK.getCode());
            item.setStatus(TransactionStatusEnum.ROLLBACK.getCode());
            txTransactionGroup.setStatus(TransactionStatusEnum.ROLLBACK.getCode());
        }else if(TransactionStatusEnum.COMMIT.getCode()==transactionStatusEnum.getCode()){
            heartBeat.setAction(NettyMessageActionEnum.COMPLETE_COMMIT.getCode());
            item.setStatus(TransactionStatusEnum.COMMIT.getCode());
            txTransactionGroup.setStatus(TransactionStatusEnum.COMMIT.getCode());
        }
        txTransactionGroup.setItemList(Collections.singletonList(item));
        heartBeat.setTxTransactionGroup(txTransactionGroup);
        return heartBeat;
    }
}
