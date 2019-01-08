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

package org.dromara.raincat.core.netty.impl;

import com.google.common.base.StandardSystemProperty;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.apache.commons.lang3.StringUtils;
import org.dromara.raincat.common.config.TxConfig;
import org.dromara.raincat.common.entity.TxManagerServer;
import org.dromara.raincat.common.enums.SerializeProtocolEnum;
import org.dromara.raincat.common.holder.LogUtil;
import org.dromara.raincat.core.netty.NettyClientService;
import org.dromara.raincat.core.netty.handler.NettyClientHandlerInitializer;
import org.dromara.raincat.core.service.impl.TxManagerLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * NettyClientServiceImpl.
 * @author xiaoyu
 */
@Service
public class NettyClientServiceImpl implements NettyClientService, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientServiceImpl.class);

    private static final String OS_NAME = "Linux";

    private EventLoopGroup workerGroup;

    private DefaultEventExecutorGroup servletExecutor;

    private String host = "127.0.0.1";

    private Integer port = 8888;

    private Channel channel;

    private Bootstrap bootstrap;

    private final NettyClientHandlerInitializer nettyClientHandlerInitializer;

    @Autowired
    public NettyClientServiceImpl(final NettyClientHandlerInitializer nettyClientHandlerInitializer) {
        this.nettyClientHandlerInitializer = nettyClientHandlerInitializer;
    }

    @Override
    public void start(final TxConfig txConfig) {
        SerializeProtocolEnum serializeProtocol =
                SerializeProtocolEnum.acquireSerializeProtocol(txConfig.getNettySerializer());
        nettyClientHandlerInitializer.setSerializeProtocolEnum(serializeProtocol);
        servletExecutor = new DefaultEventExecutorGroup(txConfig.getNettyThreadMax());
        nettyClientHandlerInitializer.setServletExecutor(servletExecutor);
        nettyClientHandlerInitializer.setTxConfig(txConfig);
        TxManagerLocator.getInstance().setTxConfig(txConfig);
        TxManagerLocator.getInstance().schedulePeriodicRefresh();
        try {
            bootstrap = new Bootstrap();
            groups(bootstrap, txConfig.getNettyThreadMax());
            doConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void groups(final Bootstrap bootstrap, final int workThreads) {
        if (Objects.equals(StandardSystemProperty.OS_NAME.value(), OS_NAME)) {
            workerGroup = new EpollEventLoopGroup(workThreads);
            bootstrap.group(workerGroup);
            bootstrap.channel(EpollSocketChannel.class);
            bootstrap.option(EpollChannelOption.TCP_CORK, true)
                    .option(EpollChannelOption.SO_KEEPALIVE, true)
                    .option(EpollChannelOption.CONNECT_TIMEOUT_MILLIS, 5)
                    .option(EpollChannelOption.SO_BACKLOG, 1024)
                    .option(EpollChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(nettyClientHandlerInitializer);
        } else {
            workerGroup = new NioEventLoopGroup(workThreads);
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(nettyClientHandlerInitializer);
        }
    }

    @Override
    public void doConnect() {
        if (channel != null && channel.isActive()) {
            return;
        }
        final TxManagerServer txManagerServer = TxManagerLocator.getInstance().locator();
        if (Objects.nonNull(txManagerServer)
                && StringUtils.isNoneBlank(txManagerServer.getHost())
                && Objects.nonNull(txManagerServer.getPort())) {
            host = txManagerServer.getHost();
            port = txManagerServer.getPort();
        }
        ChannelFuture future = bootstrap.connect(host, port);
        LogUtil.info(LOGGER, ".....connect txManager-socket -> host:port:{}", () -> host + ":" + port);
        future.addListener((ChannelFutureListener) futureListener -> {
            if (futureListener.isSuccess()) {
                channel = futureListener.channel();
                LogUtil.info(LOGGER, "Connect to server successfully!-> host:port:{}", () -> host + ":" + port);
            } else {
                LogUtil.info(LOGGER, "Failed to connect to server, try connect after 5s-> host:port:{}", () -> host + ":" + port);
                futureListener.channel().eventLoop().schedule(this::doConnect, 5, TimeUnit.SECONDS);
            }
        });

    }

    private void stop() {
        if (Objects.nonNull(servletExecutor)) {
            workerGroup.shutdownGracefully();
        }
        if (Objects.nonNull(servletExecutor)) {
            servletExecutor.shutdownGracefully();
        }

    }

    @Override
    public void destroy() {
        stop();
    }
}
