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

package org.dromara.raincat.common.netty.serizlize.hessian;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * HessianSerializeFactory.
 * @author xiaoyu
 */
public class HessianSerializeFactory extends BasePooledObjectFactory<HessianSerialize> {

    @Override
    public HessianSerialize create() {
        return createHessian();
    }

    @Override
    public PooledObject<HessianSerialize> wrap(final HessianSerialize hessian) {
        return new DefaultPooledObject<>(hessian);
    }

    private HessianSerialize createHessian() {
        return new HessianSerialize();
    }
}

