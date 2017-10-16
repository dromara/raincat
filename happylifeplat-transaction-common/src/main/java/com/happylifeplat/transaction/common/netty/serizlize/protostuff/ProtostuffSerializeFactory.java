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
package com.happylifeplat.transaction.common.netty.serizlize.protostuff;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @author xiaoyu
 */
public class ProtostuffSerializeFactory extends BasePooledObjectFactory<ProtostuffSerialize> {

    @Override
    public ProtostuffSerialize create() throws Exception {
        return createProtostuff();
    }

    @Override
    public PooledObject<ProtostuffSerialize> wrap(ProtostuffSerialize protostuffSerialize) {
        return new DefaultPooledObject<>(protostuffSerialize);
    }

    private ProtostuffSerialize createProtostuff() {
        return new ProtostuffSerialize();
    }
}
