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

package org.dromara.raincat.common.netty.serizlize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import org.dromara.raincat.common.netty.NettyTransferSerialize;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * KryoSerialize.
 * @author xiaoyu
 */
public class KryoSerialize implements NettyTransferSerialize {

    private KryoPool pool;

    public KryoSerialize(final KryoPool pool) {
        this.pool = pool;
    }

    @Override
    public void serialize(final OutputStream output, final Object object) throws IOException {
        Kryo kryo = pool.borrow();
        Output out = new Output(output);
        kryo.writeClassAndObject(out, object);
        out.close();
        output.close();
        pool.release(kryo);
    }

    @Override
    public Object deserialize(final InputStream input) throws IOException {
        Kryo kryo;
        kryo = pool.borrow();
        try (Input in = new Input(input)) {
            return kryo.readClassAndObject(in);
        } finally {
            input.close();
            pool.release(kryo);
        }
    }

}
