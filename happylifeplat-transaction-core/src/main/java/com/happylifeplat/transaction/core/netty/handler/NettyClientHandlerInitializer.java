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
package com.happylifeplat.transaction.core.netty.handler;

import com.happylifeplat.transaction.common.enums.SerializeProtocolEnum;
import com.happylifeplat.transaction.common.netty.NettyPipelineInit;
import com.happylifeplat.transaction.common.config.TxConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author xiaoyu
 */
@Component
public class NettyClientHandlerInitializer extends ChannelInitializer<SocketChannel> {


    private final NettyClientMessageHandler nettyClientMessageHandler;

    private TxConfig txConfig;

    private SerializeProtocolEnum serializeProtocolEnum;

    private DefaultEventExecutorGroup servletExecutor;

    public void setServletExecutor(DefaultEventExecutorGroup servletExecutor) {
        this.servletExecutor = servletExecutor;
    }

    public void setTxConfig(TxConfig txConfig) {
        this.txConfig = txConfig;
        nettyClientMessageHandler.setTxConfig(txConfig);
    }

    @Autowired
    public NettyClientHandlerInitializer(NettyClientMessageHandler nettyClientMessageHandler) {
        this.nettyClientMessageHandler = nettyClientMessageHandler;
    }

    public void setSerializeProtocolEnum(SerializeProtocolEnum serializeProtocolEnum) {
        this.serializeProtocolEnum = serializeProtocolEnum;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        final ChannelPipeline pipeline = socketChannel.pipeline();
        NettyPipelineInit.serializePipeline(serializeProtocolEnum, pipeline);
        pipeline.addLast("timeout", new IdleStateHandler(txConfig.getHeartTime(), txConfig.getHeartTime(), txConfig.getHeartTime(), TimeUnit.SECONDS));
        pipeline.addLast(nettyClientMessageHandler);

    }
}
