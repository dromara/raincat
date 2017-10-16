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
package com.happylifeplat.transaction.common.netty.serizlize.hessian;

import com.google.common.io.Closer;
import com.happylifeplat.transaction.common.netty.MessageCodecService;
import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author xiaoyu
 */
public class HessianCodecServiceImpl implements MessageCodecService {

    private HessianSerializePool pool = HessianSerializePool.getHessianPoolInstance();
    private static Closer closer = Closer.create();


    @Override
    public void encode(final ByteBuf out, final Object message) throws IOException {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            closer.register(byteArrayOutputStream);
            HessianSerialize hessianSerialization = pool.borrow();
            hessianSerialization.serialize(byteArrayOutputStream, message);
            byte[] body = byteArrayOutputStream.toByteArray();
            int dataLength = body.length;
            out.writeInt(dataLength);
            out.writeBytes(body);
            pool.restore(hessianSerialization);
        } finally {
            closer.close();
        }
    }

    @Override
    public Object decode(byte[] body) throws IOException {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
            closer.register(byteArrayInputStream);
            HessianSerialize hessianSerialization = pool.borrow();
            Object object = hessianSerialization.deserialize(byteArrayInputStream);
            pool.restore(hessianSerialization);
            return object;
        } finally {
            closer.close();
        }
    }
}

