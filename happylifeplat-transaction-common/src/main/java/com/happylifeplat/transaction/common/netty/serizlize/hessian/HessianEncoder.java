package com.happylifeplat.transaction.common.netty.serizlize.hessian;


import com.happylifeplat.transaction.common.netty.MessageCodecService;
import com.happylifeplat.transaction.common.netty.serizlize.MessageEncoder;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  HessianEncoder
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/18 16:47
 * @since JDK 1.8
 */
public class HessianEncoder extends MessageEncoder {

    public HessianEncoder(MessageCodecService util) {
        super(util);
    }
}

