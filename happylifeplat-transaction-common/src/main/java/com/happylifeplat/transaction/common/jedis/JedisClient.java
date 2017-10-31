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

package com.happylifeplat.transaction.common.jedis;

import java.util.Set;

/**
 * <p>Description: .</p>
 *
 * @author xiaoyu(Myth)
 * @version 1.0
 * @date 2017/10/26 14:48
 * @since JDK 1.8
 */
public interface JedisClient {


    String set(String key, String value);

    String set(String key, byte[] value);

    Long del(String... keys);

    String get(String key);

    byte[] get(byte[] key);

    Set<byte[]> keys(final byte[] pattern);

    Set<String> keys(String key);

    Long hset(String key, String item, String value);

    String hget(String key, String item);


    Long hdel(String key, String item);

    Long incr(String key);

    Long decr(String key);

    Long expire(String key, int second);


    Set<String> zrange(String key, long start, long end);


}
