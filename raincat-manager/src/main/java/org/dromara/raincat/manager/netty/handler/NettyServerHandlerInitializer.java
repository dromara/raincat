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

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.dromara.raincat.common.enums.SerializeProtocolEnum;
import org.dromara.raincat.common.netty.NettyPipelineInit;
import org.dromara.raincat.manager.config.NettyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * NettyServerHandlerInitializer.
 * @author xiaoyu
 */
@Component
public class NettyServerHandlerInitializer extends ChannelInitializer<SocketChannel> {

    private final NettyConfig nettyConfig;

    private final NettyServerMessageHandler nettyServerMessageHandler;

    private SerializeProtocolEnum serializeProtocolEnum;

    private DefaultEventExecutorGroup servletExecutor;

    @Autowired
    public NettyServerHandlerInitializer(final NettyConfig nettyConfig,
                                         final NettyServerMessageHandler nettyServerMessageHandler) {
        this.nettyConfig = nettyConfig;
        this.nettyServerMessageHandler = nettyServerMessageHandler;
    }

    public void setServletExecutor(final DefaultEventExecutorGroup servletExecutor) {
        this.servletExecutor = servletExecutor;
    }

    public void setSerializeProtocolEnum(final SerializeProtocolEnum serializeProtocolEnum) {
        this.serializeProtocolEnum = serializeProtocolEnum;
    }

    @Override
    protected void initChannel(final SocketChannel ch) {
        final ChannelPipeline pipeline = ch.pipeline();
        NettyPipelineInit.serializePipeline(serializeProtocolEnum, pipeline);
        pipeline.addLast("timeout",
                new IdleStateHandler(nettyConfig.getHeartTime(), nettyConfig.getHeartTime(), nettyConfig.getHeartTime(), TimeUnit.SECONDS));
        pipeline.addLast(nettyServerMessageHandler);
    }
}
