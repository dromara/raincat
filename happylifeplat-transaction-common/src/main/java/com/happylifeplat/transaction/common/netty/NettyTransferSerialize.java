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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author xiaoyu
 */
public interface NettyTransferSerialize {

    /**
     * netty 将object序列化成 OutputStream
     *
     * @param output OutputStream
     * @param object 对象
     * @throws IOException io异常
     */
    void serialize(OutputStream output, Object object) throws IOException;

    /**
     * netty 将 InputStream 反序列成对象
     *
     * @param input 输出流
     * @return object
     * @throws IOException io异常
     */
    Object deserialize(InputStream input) throws IOException;
}
