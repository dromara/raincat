package com.happylifeplat.transaction.common.netty.serizlize.kryo;

import com.happylifeplat.transaction.common.netty.MessageCodecService;
import com.happylifeplat.transaction.common.netty.serizlize.MessageEncoder;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/18 16:01
 * @since JDK 1.8
 */
public class KryoEncoder extends MessageEncoder {

    public KryoEncoder(MessageCodecService util) {
        super(util);
    }
}
