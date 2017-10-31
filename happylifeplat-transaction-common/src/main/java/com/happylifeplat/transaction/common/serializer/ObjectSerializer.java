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
package com.happylifeplat.transaction.common.serializer;

import com.happylifeplat.transaction.common.exception.TransactionException;

/**
 * @author xiaoyu
 */
public interface ObjectSerializer {
    /**
     * 序列化对象
     *
     * @param obj 需要序更列化的对象
     * @return byte []
     * @throws TransactionException 异常
     */
    byte[] serialize(Object obj) throws TransactionException;

    /**
     * 反序列化对象
     *
     * @param param 需要反序列化的byte []
     * @return 对象
     * @throws TransactionException 异常
     */

    /**
     * 反序列化对象
     *
     * @param param 需要反序列化的byte []
     * @param clazz 序列化后对应的java class
     * @param <T>   泛型
     * @return <T>
     * @throws TransactionException 异常
     */
    <T> T deSerialize(byte[] param, Class<T> clazz) throws TransactionException;

    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    String getScheme();
}
