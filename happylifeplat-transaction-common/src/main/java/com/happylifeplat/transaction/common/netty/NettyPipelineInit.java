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
package com.happylifeplat.transaction.common.netty;

import com.happylifeplat.transaction.common.enums.SerializeProtocolEnum;
import com.happylifeplat.transaction.common.netty.serizlize.hessian.HessianCodecServiceImpl;
import com.happylifeplat.transaction.common.netty.serizlize.hessian.HessianDecoder;
import com.happylifeplat.transaction.common.netty.serizlize.hessian.HessianEncoder;
import com.happylifeplat.transaction.common.netty.serizlize.kryo.KryoCodecServiceImpl;
import com.happylifeplat.transaction.common.netty.serizlize.kryo.KryoDecoder;
import com.happylifeplat.transaction.common.netty.serizlize.kryo.KryoEncoder;
import com.happylifeplat.transaction.common.netty.serizlize.kryo.KryoPoolFactory;
import com.happylifeplat.transaction.common.netty.serizlize.protostuff.ProtostuffCodecServiceImpl;
import com.happylifeplat.transaction.common.netty.serizlize.protostuff.ProtostuffDecoder;
import com.happylifeplat.transaction.common.netty.serizlize.protostuff.ProtostuffEncoder;
import io.netty.channel.ChannelPipeline;

/**
 * @author xiaoyu
 */
public class NettyPipelineInit {
    public static void serializePipeline(SerializeProtocolEnum serializeProtocol, ChannelPipeline pipeline) {
        switch (serializeProtocol) {
            case KRYO:
                KryoCodecServiceImpl kryoCodecServiceImpl = new KryoCodecServiceImpl(KryoPoolFactory.getKryoPoolInstance());
                pipeline.addLast(new KryoEncoder(kryoCodecServiceImpl));
                pipeline.addLast(new KryoDecoder(kryoCodecServiceImpl));
                break;
            case HESSIAN:
                HessianCodecServiceImpl hessianCodecServiceImpl = new HessianCodecServiceImpl();
                pipeline.addLast(new HessianEncoder(hessianCodecServiceImpl));
                pipeline.addLast(new HessianDecoder(hessianCodecServiceImpl));
                break;
            case PROTOSTUFF:
                ProtostuffCodecServiceImpl protostuffCodecServiceImpl = new ProtostuffCodecServiceImpl();
                pipeline.addLast(new ProtostuffEncoder(protostuffCodecServiceImpl));
                pipeline.addLast(new ProtostuffDecoder(protostuffCodecServiceImpl));
                break;
            default:
                KryoCodecServiceImpl defaultCodec = new KryoCodecServiceImpl(KryoPoolFactory.getKryoPoolInstance());
                pipeline.addLast(new KryoEncoder(defaultCodec));
                pipeline.addLast(new KryoDecoder(defaultCodec));
                break;
        }
    }
}
