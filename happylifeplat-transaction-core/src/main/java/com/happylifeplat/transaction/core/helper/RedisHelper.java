package com.happylifeplat.transaction.core.helper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * redis帮助类
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/5/27 11:56
 * @since JDK 1.8
 */
public class RedisHelper {

    public static byte[] getRedisKey(String keyPrefix, String id) {
        byte[] prefix = keyPrefix.getBytes();
        final byte[] idBytes = id.getBytes();
        byte[] key = new byte[prefix.length + idBytes.length];
        System.arraycopy(prefix, 0, key, 0, prefix.length);
        System.arraycopy(idBytes, 0, key, prefix.length, idBytes.length);
        return key;
    }

    public static byte[] getKeyValue(JedisPool jedisPool, final byte[] key) {
        return execute(jedisPool, jedis -> {
                    Map<byte[], byte[]> fieldValueMap = jedis.hgetAll(key);
                    List<Map.Entry<byte[], byte[]>> entries = new ArrayList<>(fieldValueMap.entrySet());
                    entries.sort((entry1, entry2) -> (int) (ByteUtils.bytesToLong(entry1.getKey()) - ByteUtils.bytesToLong(entry2.getKey())));
                    if (entries.isEmpty())
                        return null;
                    return entries.get(entries.size() - 1).getValue();
                }
        );
    }

    public static byte[] getKeyValue(Jedis jedis, final byte[] key) {
        Map<byte[], byte[]> fieldValueMap = jedis.hgetAll(key);
        List<Map.Entry<byte[], byte[]>> entries = new ArrayList<Map.Entry<byte[], byte[]>>(fieldValueMap.entrySet());
        entries.sort((entry1, entry2) -> (int) (ByteUtils.bytesToLong(entry1.getKey()) - ByteUtils.bytesToLong(entry2.getKey())));
        if (entries.isEmpty())
            return null;
        return entries.get(entries.size() - 1).getValue();
    }

    public static <T> T execute(JedisPool jedisPool, JedisCallback<T> callback) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return callback.doInJedis(jedis);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}