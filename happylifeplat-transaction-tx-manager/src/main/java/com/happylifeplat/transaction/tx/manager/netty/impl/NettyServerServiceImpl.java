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
package com.happylifeplat.transaction.tx.manager.netty.impl;

import com.google.common.base.StandardSystemProperty;
import com.happylifeplat.transaction.common.enums.SerializeProtocolEnum;
import com.happylifeplat.transaction.common.exception.TransactionRuntimeException;
import com.happylifeplat.transaction.tx.manager.config.NettyConfig;
import com.happylifeplat.transaction.tx.manager.netty.NettyService;
import com.happylifeplat.transaction.tx.manager.netty.handler.NettyServerHandlerInitializer;
import com.happylifeplat.transaction.tx.manager.service.TxManagerService;
import com.happylifeplat.transaction.tx.manager.socket.SocketManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author xiaoyu
 */
@Component
public class NettyServerServiceImpl implements NettyService {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerServiceImpl.class);

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private DefaultEventExecutorGroup servletExecutor;
    private static int MAX_THREADS = Runtime.getRuntime().availableProcessors() << 1;

    private static final String OS_NAME = "Linux";

    private final TxManagerService txManagerService;

    private final NettyConfig nettyConfig;

    private final NettyServerHandlerInitializer nettyServerHandlerInitializer;

    @Autowired(required = false)
    public NettyServerServiceImpl(TxManagerService txManagerService, NettyConfig nettyConfig, NettyServerHandlerInitializer nettyServerHandlerInitializer) {
        this.txManagerService = txManagerService;
        this.nettyConfig = nettyConfig;
        this.nettyServerHandlerInitializer = nettyServerHandlerInitializer;
    }

    /**
     * 启动netty服务
     */
    @Override
    public void start() {
        SocketManager.getInstance().setMaxConnection(nettyConfig.getMaxConnection());
        servletExecutor = new DefaultEventExecutorGroup(MAX_THREADS);
        if (nettyConfig.getMaxThreads() != 0) {
            MAX_THREADS = nettyConfig.getMaxThreads();
        }
        try {
            final SerializeProtocolEnum serializeProtocolEnum =
                    SerializeProtocolEnum.acquireSerializeProtocol(nettyConfig.getSerialize());
            nettyServerHandlerInitializer.setSerializeProtocolEnum(serializeProtocolEnum);
            nettyServerHandlerInitializer.setServletExecutor(servletExecutor);
            ServerBootstrap b = new ServerBootstrap();
            groups(b, MAX_THREADS << 1);
          /*  bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup(MAX_THREADS * 2);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childHandler(nettyServerHandlerInitializer);*/
            b.bind(nettyConfig.getPort());
            LOGGER.info("netty service started on port: " + nettyConfig.getPort());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void groups(ServerBootstrap b, int workThreads) {
        if (Objects.equals(StandardSystemProperty.OS_NAME.value(), OS_NAME)) {
            bossGroup = new EpollEventLoopGroup(1);
            workerGroup = new EpollEventLoopGroup(workThreads);
            b.group(bossGroup, workerGroup)
                    .channel(EpollServerSocketChannel.class)
                    .option(EpollChannelOption.TCP_CORK, true)
                    .option(EpollChannelOption.SO_KEEPALIVE, true)
                    .option(EpollChannelOption.SO_BACKLOG, 100)
                    .option(EpollChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(EpollChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(nettyServerHandlerInitializer);
        } else {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup(workThreads);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 100)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(nettyServerHandlerInitializer);
        }
    }


    /**
     * 关闭服务
     */
    @Override
    public void stop() {
        try {
            if (null != bossGroup) {
                bossGroup.shutdownGracefully().await();
            }
            if (null != workerGroup) {
                workerGroup.shutdownGracefully().await();
            }
            if (null != servletExecutor) {
                servletExecutor.shutdownGracefully().await();
            }
        } catch (InterruptedException e) {
            throw new TransactionRuntimeException(" Netty  Container stop interrupted", e);
        }

    }


}
