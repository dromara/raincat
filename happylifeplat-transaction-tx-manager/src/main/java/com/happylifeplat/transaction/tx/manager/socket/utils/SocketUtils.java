package com.happylifeplat.transaction.tx.manager.socket.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  SocketUtils
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/13 18:19
 * @since JDK 1.8
 */
public class SocketUtils {

    public static String getJson(Object msg) {
        String json;
        try {
            ByteBuf buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            json = new String(bytes);
        } finally {
            ReferenceCountUtil.release(msg);
        }
        return json;

    }

    public static void sendMsg(ChannelHandlerContext ctx, String msg){
        ctx.writeAndFlush(Unpooled.buffer().writeBytes(msg.getBytes()));
    }


    public static void sendMsg(Channel ctx, String msg){
        ctx.writeAndFlush(Unpooled.buffer().writeBytes(msg.getBytes()));
    }
}
