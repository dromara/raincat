
package com.happylifeplat.transaction.tx.manager.netty.handler;

import com.happylifeplat.transaction.common.enums.SerializeProtocolEnum;
import com.happylifeplat.transaction.common.netty.NettyPipelineInit;
import com.happylifeplat.transaction.common.netty.serizlize.hessian.HessianCodecService;
import com.happylifeplat.transaction.common.netty.serizlize.hessian.HessianDecoder;
import com.happylifeplat.transaction.common.netty.serizlize.hessian.HessianEncoder;
import com.happylifeplat.transaction.common.netty.serizlize.kryo.KryoCodecService;
import com.happylifeplat.transaction.common.netty.serizlize.kryo.KryoDecoder;
import com.happylifeplat.transaction.common.netty.serizlize.kryo.KryoEncoder;
import com.happylifeplat.transaction.common.netty.serizlize.kryo.KryoPoolFactory;
import com.happylifeplat.transaction.common.netty.serizlize.protostuff.ProtostuffCodecService;
import com.happylifeplat.transaction.common.netty.serizlize.protostuff.ProtostuffDecoder;
import com.happylifeplat.transaction.common.netty.serizlize.protostuff.ProtostuffEncoder;
import com.happylifeplat.transaction.tx.manager.config.NettyConfig;
import com.happylifeplat.transaction.tx.manager.service.TxManagerService;
import com.netflix.discovery.converters.Auto;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * netty服务初始化
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/5/11 18:02
 * @since JDK 1.8
 */
@Component
public class NettyServerHandlerInitializer extends ChannelInitializer<SocketChannel> {

    private final NettyConfig nettyConfig;


    private final NettyServerMessageHandler nettyServerMessageHandler;

    private SerializeProtocolEnum serializeProtocolEnum;


    private DefaultEventExecutorGroup servletExecutor;

    public void setServletExecutor(DefaultEventExecutorGroup servletExecutor) {
        this.servletExecutor = servletExecutor;
    }

    @Autowired
    public NettyServerHandlerInitializer(NettyConfig nettyConfig, NettyServerMessageHandler nettyServerMessageHandler) {
        this.nettyConfig = nettyConfig;
        this.nettyServerMessageHandler = nettyServerMessageHandler;
    }

    public void setSerializeProtocolEnum(SerializeProtocolEnum serializeProtocolEnum) {
        this.serializeProtocolEnum = serializeProtocolEnum;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();
        NettyPipelineInit.serializePipeline(serializeProtocolEnum,pipeline);
        pipeline.addLast("timeout",
                new IdleStateHandler(nettyConfig.getHeartTime(), nettyConfig.getHeartTime(), nettyConfig.getHeartTime(), TimeUnit.SECONDS));
        pipeline.addLast(nettyServerMessageHandler);
    }
}
