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

package com.happylifeplat.transaction.tx.manager.configuration.redis;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClusterConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;

/**
 * @author xiaoyu
 */
public class RedisClusterConfig {

    @Autowired
    private RedisProperties redisProperties;


    @Bean
    public RedisClusterConfiguration getClusterConfiguration() {
        Map<String, Object> source = Maps.newHashMap();
        source.put("spring.redis.cluster.nodes", redisProperties.getClusterNodes());
        source.put("spring.redis.cluster.max-redirects", redisProperties.getMaxRedirects());
        return new RedisClusterConfiguration(new MapPropertySource("RedisClusterConfiguration", source));

    }

    @Bean
    public JedisPoolConfig getJedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(redisProperties.getMaxTotal());
        jedisPoolConfig.setMaxIdle(redisProperties.getMaxIdle());
        jedisPoolConfig.setMinIdle(redisProperties.getMinIdle());
        jedisPoolConfig.setMaxWaitMillis(redisProperties.getMaxWaitMillis());
        return jedisPoolConfig;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory(getClusterConfiguration(),getJedisPoolConfig());
    }


    @Bean
    public JedisClusterConnection getJedisClusterConnection() {
        return (JedisClusterConnection) jedisConnectionFactory().getConnection();
    }

    @Bean
    public RedisTemplate getStringRedisTemplate() {
        StringRedisTemplate clusterTemplate = new StringRedisTemplate();
        clusterTemplate.setConnectionFactory(jedisConnectionFactory());
        clusterTemplate.setKeySerializer(new StringRedisSerializer());
        clusterTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return clusterTemplate;
    }


}
