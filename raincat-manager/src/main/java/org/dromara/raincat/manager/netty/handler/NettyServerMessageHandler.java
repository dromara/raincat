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

package org.dromara.raincat.manager.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.raincat.common.enums.NettyMessageActionEnum;
import org.dromara.raincat.common.enums.NettyResultEnum;
import org.dromara.raincat.common.holder.LogUtil;
import org.dromara.raincat.common.netty.bean.HeartBeat;
import org.dromara.raincat.common.netty.bean.TxTransactionGroup;
import org.dromara.raincat.common.netty.bean.TxTransactionItem;
import org.dromara.raincat.manager.config.Address;
import org.dromara.raincat.manager.service.TxManagerService;
import org.dromara.raincat.manager.service.TxTransactionExecutor;
import org.dromara.raincat.manager.socket.SocketManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * NettyServerMessageHandler.
 * @author xiaoyu
 */
@ChannelHandler.Sharable
@Component
public class NettyServerMessageHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerMessageHandler.class);

    private final TxManagerService txManagerService;

    private final TxTransactionExecutor txTransactionExecutor;

    @Autowired
    public NettyServerMessageHandler(final TxManagerService txManagerService,
                                     final TxTransactionExecutor txTransactionExecutor) {
        this.txManagerService = txManagerService;
        this.txTransactionExecutor = txTransactionExecutor;
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        HeartBeat hb = (HeartBeat) msg;
        TxTransactionGroup txTransactionGroup = hb.getTxTransactionGroup();
        try {
            final NettyMessageActionEnum actionEnum = NettyMessageActionEnum.acquireByCode(hb.getAction());
            LogUtil.debug(LOGGER, "receive client date this action:{}", actionEnum::getDesc);
            Boolean success;
            switch (actionEnum) {
                case HEART:
                    hb.setAction(NettyMessageActionEnum.HEART.getCode());
                    ctx.writeAndFlush(hb);
                    break;
                case CREATE_GROUP:
                    final List<TxTransactionItem> items = txTransactionGroup.getItemList();
                    if (CollectionUtils.isNotEmpty(items)) {
                        String modelName = ctx.channel().remoteAddress().toString();
                        //这里创建事务组的时候，事务组也作为第一条数据来存储
                        //第二条数据才是发起方 因此是get(1)
                        final TxTransactionItem item = items.get(1);
                        item.setModelName(modelName);
                        item.setTmDomain(Address.getInstance().getDomain());
                    }
                    success = txManagerService.saveTxTransactionGroup(txTransactionGroup);
                    ctx.writeAndFlush(buildSendMessage(hb.getKey(), success));
                    break;
                case ADD_TRANSACTION:
                    final List<TxTransactionItem> itemList = txTransactionGroup.getItemList();
                    if (CollectionUtils.isNotEmpty(itemList)) {
                        String modelName = ctx.channel().remoteAddress().toString();
                        final TxTransactionItem item = itemList.get(0);
                        item.setModelName(modelName);
                        item.setTmDomain(Address.getInstance().getDomain());
                        success = txManagerService.addTxTransaction(txTransactionGroup.getId(), item);
                        ctx.writeAndFlush(buildSendMessage(hb.getKey(), success));
                    }
                    break;
                case GET_TRANSACTION_GROUP_STATUS:
                    final int status = txManagerService.findTxTransactionGroupStatus(txTransactionGroup.getId());
                    txTransactionGroup.setStatus(status);
                    hb.setTxTransactionGroup(txTransactionGroup);
                    ctx.writeAndFlush(hb);
                    break;
                case FIND_TRANSACTION_GROUP_INFO:
                    final List<TxTransactionItem> txTransactionItems = txManagerService.listByTxGroupId(txTransactionGroup.getId());
                    txTransactionGroup.setItemList(txTransactionItems);
                    hb.setTxTransactionGroup(txTransactionGroup);
                    ctx.writeAndFlush(hb);
                    break;
                case ROLLBACK:
                    ctx.writeAndFlush(buildSendMessage(hb.getKey(), true));
                    //收到客户端的回滚通知  此通知为事务发起（start）里面通知的
                    final String groupId = txTransactionGroup.getId();
                    txTransactionExecutor.rollBack(groupId);
                    break;
                case PRE_COMMIT:
                    ctx.writeAndFlush(buildSendMessage(hb.getKey(), true));
                    txTransactionExecutor.preCommit(txTransactionGroup.getId());
                    break;
                case COMPLETE_COMMIT:
                    final List<TxTransactionItem> its = txTransactionGroup.getItemList();
                    if (CollectionUtils.isNotEmpty(its)) {
                        final TxTransactionItem item = its.get(0);
                        txManagerService.updateTxTransactionItemStatus(txTransactionGroup.getId(),
                                item.getTaskKey(),
                                item.getStatus(), item.getMessage());
                        //异步的情况下，就不要返回消息了（key为空就是异步）
                        if(StringUtils.isNotBlank(hb.getKey())){
                            ctx.writeAndFlush(buildSendMessage(hb.getKey(), true));
                        }
                    }
                    break;
                default:
                    hb.setAction(NettyMessageActionEnum.HEART.getCode());
                    ctx.writeAndFlush(hb);
                    break;
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        //是否到达最大上线连接数
        if (SocketManager.getInstance().isAllowConnection()) {
            SocketManager.getInstance().addClient(ctx.channel());
        } else {
            ctx.close();
        }
    }

    @Override
    public void channelRegistered(final ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
        SocketManager.getInstance().removeClient(ctx.channel());
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) {
        //心跳配置
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                ctx.close();
            }
        }
    }

    private HeartBeat buildSendMessage(final String key, final Boolean success) {
        HeartBeat heartBeat = new HeartBeat();
        heartBeat.setKey(key);
        heartBeat.setAction(NettyMessageActionEnum.RECEIVE.getCode());
        if (success) {
            heartBeat.setResult(NettyResultEnum.SUCCESS.getCode());
        } else {
            heartBeat.setResult(NettyResultEnum.FAIL.getCode());
        }
        return heartBeat;
    }

}