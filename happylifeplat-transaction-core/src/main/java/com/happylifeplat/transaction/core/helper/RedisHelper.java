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
package com.happylifeplat.transaction.core.helper;

import com.happylifeplat.transaction.common.holder.RedisKeyUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xiaoyu
 */
public class RedisHelper {

    public static byte[] getRedisKey(String keyPrefix, String id) {
        return RedisKeyUtils.getRedisKey(keyPrefix, id);
    }

    public static String buildRecoverKey(String keyPrefix, String id) {
        return String.join(":", keyPrefix, id);
    }


    public static <T> T execute(JedisPool jedisPool, JedisCallback<T> callback) {
        try(Jedis jedis=jedisPool.getResource()) {
            return callback.doInJedis(jedis);
        }
    }
}