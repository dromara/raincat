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

package org.dromara.raincat.core.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.ScheduledFuture;
import org.dromara.raincat.common.config.TxConfig;
import org.dromara.raincat.common.enums.NettyMessageActionEnum;
import org.dromara.raincat.common.enums.NettyResultEnum;
import org.dromara.raincat.common.holder.CollectionUtils;
import org.dromara.raincat.common.holder.IdWorkerUtils;
import org.dromara.raincat.common.holder.LogUtil;
import org.dromara.raincat.common.netty.bean.HeartBeat;
import org.dromara.raincat.common.netty.bean.TxTransactionGroup;
import org.dromara.raincat.common.netty.bean.TxTransactionItem;
import org.dromara.raincat.core.concurrent.task.BlockTask;
import org.dromara.raincat.core.concurrent.task.BlockTaskHelper;
import org.dromara.raincat.core.helper.SpringBeanUtils;
import org.dromara.raincat.core.netty.NettyClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * NettyClientMessageHandler.
 * @author xiaoyu
 */
@Component
@ChannelHandler.Sharable
public class NettyClientMessageHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientMessageHandler.class);

    private static final HeartBeat HEART_BEAT = new HeartBeat();

    private static volatile ChannelHandlerContext ctx;

    private TxConfig txConfig;

    public void setTxConfig(final TxConfig txConfig) {
        this.txConfig = txConfig;
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        HeartBeat heartBeat = (HeartBeat) msg;
        final NettyMessageActionEnum actionEnum = NettyMessageActionEnum.acquireByCode(heartBeat.getAction());
        LogUtil.debug(LOGGER, "receive tx manage info :{}", actionEnum::getDesc);
       /* executorService.execute(() -> {*/
        try {
            switch (actionEnum) {
                case HEART:
                    break;
                case RECEIVE:
                    receivedCommand(heartBeat.getKey(), heartBeat.getResult());
                    break;
                case ROLLBACK:
                    notify(heartBeat);
                    break;
                case COMPLETE_COMMIT:
                    notify(heartBeat);
                    break;
                case GET_TRANSACTION_GROUP_STATUS:
                    final BlockTask blockTask = BlockTaskHelper.getInstance().getTask(heartBeat.getKey());
                    final TxTransactionGroup txTransactionGroup = heartBeat.getTxTransactionGroup();
                    blockTask.setAsyncCall(objects -> txTransactionGroup.getStatus());
                    blockTask.signal();
                    break;
                case FIND_TRANSACTION_GROUP_INFO:
                    final BlockTask task = BlockTaskHelper.getInstance().getTask(heartBeat.getKey());
                    task.setAsyncCall(objects -> heartBeat.getTxTransactionGroup());
                    task.signal();
                    break;
                default:
                    break;

            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
       /* });*/

    }

    private void notify(final HeartBeat heartBeat) {
        final List<TxTransactionItem> txTransactionItems =
                heartBeat.getTxTransactionGroup().getItemList();
        if (CollectionUtils.isNotEmpty(txTransactionItems)) {
            final TxTransactionItem item = txTransactionItems.get(0);
            final BlockTask task = BlockTaskHelper.getInstance().getTask(item.getTaskKey());
            task.setAsyncCall(objects -> item.getStatus());
            task.signal();
        }
    }

    private void receivedCommand(final String key, final int result) {
        final BlockTask blockTask = BlockTaskHelper.getInstance().getTask(key);
        if (Objects.nonNull(blockTask)) {
            blockTask.setAsyncCall(objects -> result == NettyResultEnum.SUCCESS.getCode());
            blockTask.signal();
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("close to tx manager");
        super.channelInactive(ctx);
        SpringBeanUtils.getInstance().getBean(NettyClientService.class).doConnect();

    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        NettyClientMessageHandler.ctx = ctx;
        LOGGER.info("connected tx manager-->" + ctx);
    }

    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) {
        //心跳配置
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                SpringBeanUtils.getInstance().getBean(NettyClientService.class).doConnect();
            } else if (event.state() == IdleState.WRITER_IDLE) {
                //表示已经多久没有发送数据了
                HEART_BEAT.setAction(NettyMessageActionEnum.HEART.getCode());
                ctx.writeAndFlush(HEART_BEAT);
                LogUtil.debug(LOGGER, () -> "send tx manager heart beat!");
            } else if (event.state() == IdleState.ALL_IDLE) {
                //表示已经多久既没有收到也没有发送数据了
                SpringBeanUtils.getInstance().getBean(NettyClientService.class).doConnect();
            }
        }
    }


    /**
     * send message to tx manager .
     *
     * @param heartBeat {@linkplain HeartBeat }
     * @return Object
     */
    public Object sendTxManagerMessage(final HeartBeat heartBeat) {
        if (ctx != null && ctx.channel() != null && ctx.channel().isActive()) {
            final String sendKey = IdWorkerUtils.getInstance().createTaskKey();
            BlockTask sendTask = BlockTaskHelper.getInstance().getTask(sendKey);
            heartBeat.setKey(sendKey);
            ctx.writeAndFlush(heartBeat);
            final ScheduledFuture<?> schedule =
                    ctx.executor().schedule(() -> {
                        if (!sendTask.isNotify()) {
                            if (NettyMessageActionEnum.GET_TRANSACTION_GROUP_STATUS.getCode()
                                    == heartBeat.getAction()) {
                                sendTask.setAsyncCall(objects -> NettyResultEnum.TIME_OUT.getCode());
                            } else if (NettyMessageActionEnum.FIND_TRANSACTION_GROUP_INFO.getCode()
                                    == heartBeat.getAction()) {
                                sendTask.setAsyncCall(objects -> null);
                            } else {
                                sendTask.setAsyncCall(objects -> false);
                            }
                            sendTask.signal();
                        }
                    }, txConfig.getDelayTime(), TimeUnit.SECONDS);
            //发送线程在此等待，等tm是否 正确返回（正确返回唤醒） 返回错误或者无返回通过上面的调度线程唤醒
            sendTask.await();
            //如果已经被唤醒，就不需要去执行调度线程了 ，关闭上面的调度线程池中的任务
            if (!schedule.isDone()) {
                schedule.cancel(false);
            }
            try {
                return sendTask.getAsyncCall().callBack();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                return null;
            } finally {
                BlockTaskHelper.getInstance().removeByKey(sendKey);
            }
        } else {
            return null;
        }

    }


    /**
     * async Send message to tx Manager.
     *
     * @param heartBeat {@linkplain HeartBeat }
     */
    public void asyncSendTxManagerMessage(final HeartBeat heartBeat) {
        if (ctx != null && ctx.channel() != null && ctx.channel().isActive()) {
            ctx.writeAndFlush(heartBeat);
        }
    }

}
