package com.happylifeplat.transaction.common.netty;

import com.happylifeplat.transaction.common.enums.SerializeProtocolEnum;
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
import io.netty.channel.ChannelPipeline;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  NettyPipelineInit
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/18 17:46
 * @since JDK 1.8
 */
public class NettyPipelineInit {
    public static void serializePipeline(SerializeProtocolEnum serializeProtocol, ChannelPipeline pipeline) {
        switch (serializeProtocol) {
            case KRYO:
                KryoCodecService kryoCodecService = new KryoCodecService(KryoPoolFactory.getKryoPoolInstance());
                pipeline.addLast(new KryoEncoder(kryoCodecService));
                pipeline.addLast(new KryoDecoder(kryoCodecService));
                break;
            case HESSIAN:
                HessianCodecService hessianCodecService = new HessianCodecService();
                pipeline.addLast(new HessianEncoder(hessianCodecService));
                pipeline.addLast(new HessianDecoder(hessianCodecService));
                break;
            case PROTOSTUFF:
                ProtostuffCodecService protostuffCodecService = new ProtostuffCodecService();
                pipeline.addLast(new ProtostuffEncoder(protostuffCodecService));
                pipeline.addLast(new ProtostuffDecoder(protostuffCodecService));
                break;
            default:
                KryoCodecService defaultCodec = new KryoCodecService(KryoPoolFactory.getKryoPoolInstance());
                pipeline.addLast(new KryoEncoder(defaultCodec));
                pipeline.addLast(new KryoDecoder(defaultCodec));
                break;
        }
    }
}
