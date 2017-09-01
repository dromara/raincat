package com.happylifeplat.transaction.core.helper;

import redis.clients.jedis.Jedis;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * JedisCallback 帮助类
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/5/27 11:56
 * @since JDK 1.8
 */
@FunctionalInterface
public interface JedisCallback<T> {

    T doInJedis(Jedis jedis);
}