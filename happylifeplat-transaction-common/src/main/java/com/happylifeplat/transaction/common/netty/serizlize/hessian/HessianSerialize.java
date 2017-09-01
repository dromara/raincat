package com.happylifeplat.transaction.common.netty.serizlize.hessian;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.happylifeplat.transaction.common.netty.NettyTransferSerialize;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  HessianSerialize
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/18 16:47
 * @since JDK 1.8
 */
public class HessianSerialize implements NettyTransferSerialize {

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
