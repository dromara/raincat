package com.happylifeplat.transaction.common.netty;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  netty 数据传输序列化
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/18 16:13
 * @since JDK 1.8
 */
public interface NettyTransferSerialize {

    void serialize(OutputStream output, Object object) throws IOException;

    Object deserialize(InputStream input) throws IOException;
}
