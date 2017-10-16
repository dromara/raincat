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

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author xiaoyu
 */
public interface MessageCodecService {

    /**
     * 消息定长
     */
    int MESSAGE_LENGTH = 4;


    /**
     * netty 将java对象转成byteBuf
     *
     * @param out     输出byteBuf
     * @param message 对象信息
     * @throws IOException io异常
     */
    void encode(final ByteBuf out, final Object message) throws IOException;


    /**
     * netty 将java对象转成byteBuf
     * @param out 输出byteBuf
     * @param message  对象信息
     * @throws IOException io异常
     */

    /**
     * netty 将byteBuf转成java对象
     *
     * @param body byte数组
     * @return Object
     * @throws IOException io异常
     */
    Object decode(byte[] body) throws IOException;
}
