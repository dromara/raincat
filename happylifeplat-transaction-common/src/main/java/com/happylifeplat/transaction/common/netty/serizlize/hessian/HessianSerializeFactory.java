
package com.happylifeplat.transaction.common.netty.serizlize.hessian;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;


/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  HessianSerializeFactory
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/18 16:47
 * @since JDK 1.8
 */
public class HessianSerializeFactory extends BasePooledObjectFactory<HessianSerialize> {

    public HessianSerialize create() throws Exception {
        return createHessian();
    }

    public PooledObject<HessianSerialize> wrap(HessianSerialize hessian) {
        return new DefaultPooledObject<>(hessian);
    }

    private HessianSerialize createHessian() {
        return new HessianSerialize();
    }
}

