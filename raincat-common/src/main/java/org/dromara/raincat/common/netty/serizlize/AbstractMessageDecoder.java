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

package org.dromara.raincat.common.netty.serizlize;

import org.dromara.raincat.common.netty.MessageCodecService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AbstractMessageDecoder.
 * @author xiaoyu
 */
public abstract class AbstractMessageDecoder extends ByteToMessageDecoder {

    private static final int MESSAGE_LENGTH = MessageCodecService.MESSAGE_LENGTH;

    private MessageCodecService util;

    public AbstractMessageDecoder(final MessageCodecService service) {
        this.util = service;
    }

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) {
        if (in.readableBytes() < AbstractMessageDecoder.MESSAGE_LENGTH) {
            return;
        }
        in.markReaderIndex();
        int messageLength = in.readInt();
        if (messageLength < 0) {
            ctx.close();
        }
        if (in.readableBytes() < messageLength) {
            in.resetReaderIndex();
        } else {
            byte[] messageBody = new byte[messageLength];
            in.readBytes(messageBody);
            try {
                Object obj = util.decode(messageBody);
                out.add(obj);
            } catch (IOException ex) {
                Logger.getLogger(AbstractMessageDecoder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

