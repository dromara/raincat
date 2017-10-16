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

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.happylifeplat.transaction.common.netty.NettyTransferSerialize;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author xiaoyu
 */
public class HessianSerialize implements NettyTransferSerialize {

    @Override
    public void serialize(OutputStream output, Object object) {
        Hessian2Output ho = new Hessian2Output(output);
        try {
            ho.startMessage();
            ho.writeObject(object);
            ho.completeMessage();
            ho.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object deserialize(InputStream input) {
        Object result = null;
        try {
            Hessian2Input hi = new Hessian2Input(input);
            hi.startMessage();
            result = hi.readObject();
            hi.completeMessage();
            hi.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
