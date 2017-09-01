
package com.happylifeplat.transaction.core.spi;

import com.happylifeplat.transaction.common.exception.TransactionException;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  序列化接口
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/5/27 13:51
 * @since JDK 1.8
 */
public interface ObjectSerializer {
    /**
     * 序列化对象
     *
     * @param obj 需要序更列化的对象
     * @return byte []
     * @throws TransactionException 异常
     */
    byte[] serialize(Object obj) throws TransactionException;

    /**
     * 反序列化对象
     *
     * @param param 需要反序列化的byte []
     * @return 对象
     * @throws TransactionException 异常
     */
    <T> T deSerialize(byte[] param, Class<T> clazz) throws TransactionException;

    /**
     * 设置scheme
     * @return scheme 命名
     */
    String getScheme();
}
