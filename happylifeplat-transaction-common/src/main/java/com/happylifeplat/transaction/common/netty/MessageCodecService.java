package com.happylifeplat.transaction.common.netty;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  netty数据序列化服务
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/18 15:47
 * @since JDK 1.8
 */
public interface MessageCodecService {

    int MESSAGE_LENGTH = 4;

    void encode(final ByteBuf out, final Object message) throws IOException;

    Object decode(byte[] body) throws IOException;
}
