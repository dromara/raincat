package com.happylifeplat.transaction.core.netty.handler;

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
import com.happylifeplat.transaction.core.config.TxConfig;
import com.happylifeplat.transaction.core.netty.NettyClientService;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  NettyClientHandlerInitializer
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/13 20:38
 * @since JDK 1.8
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
        NettyPipelineInit.serializePipeline(serializeProtocolEnum,pipeline);
        pipeline.addLast("timeout", new IdleStateHandler(txConfig.getHeartTime(), txConfig.getHeartTime(), txConfig.getHeartTime(), TimeUnit.SECONDS));
        pipeline.addLast(nettyClientMessageHandler);

    }
}
