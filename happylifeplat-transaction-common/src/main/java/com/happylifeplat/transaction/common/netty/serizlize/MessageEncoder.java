
package com.happylifeplat.transaction.common.netty.serizlize;

import com.happylifeplat.transaction.common.netty.MessageCodecService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * 继承netty的MessageToByteEncoder,自定义反序列化
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/18 15:47
 * @since JDK 1.8
 */
public abstract class MessageEncoder extends MessageToByteEncoder<Object> {

    private MessageCodecService util = null;

    public MessageEncoder(final MessageCodecService util) {
        this.util = util;
    }

    protected void encode(final ChannelHandlerContext ctx, final Object msg, final ByteBuf out) throws Exception {
        util.encode(out, msg);
    }
}

