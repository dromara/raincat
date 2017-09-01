package com.happylifeplat.transaction.common.netty.serizlize.hessian;


import com.happylifeplat.transaction.common.netty.MessageCodecService;
import com.happylifeplat.transaction.common.netty.serizlize.MessageDecoder;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  HessianCodecService
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/18 15:47
 * @since JDK 1.8
 */
public class HessianDecoder extends MessageDecoder {

    public HessianDecoder(MessageCodecService util) {
        super(util);
    }
}

