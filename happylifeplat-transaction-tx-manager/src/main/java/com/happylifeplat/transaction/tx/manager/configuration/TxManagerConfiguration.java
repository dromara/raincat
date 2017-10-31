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
package com.happylifeplat.transaction.tx.manager.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.happylifeplat.transaction.tx.manager.config.NettyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Arrays;
import java.util.Map;

/**
 * @author xiaoyu
 */
@Configuration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class TxManagerConfiguration {


    @Configuration
    static class NettyConfiguration {

        @Bean
        @ConfigurationProperties("tx.manager.netty")
        public NettyConfig getNettyConfig() {
            return new NettyConfig();
        }


    }


    @Configuration
    static class RestConfiguration {
        @Bean
        public RestTemplate getRestTemplate() {
            return new RestTemplate();
        }
    }

    @Configuration
    static class RedisConfiguration {


        private final Environment env;

        @Autowired
        public RedisConfiguration(Environment env) {
            this.env = env;
        }


        @Bean
        public KeyGenerator keyGenerator() {
            return (target, method, params) -> {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                return Arrays.stream(params)
                        .map(obj -> sb.append(obj.toString())).toString();

            };
        }

        @Bean
        @ConfigurationProperties(prefix = "tx.redis")
        public JedisPoolConfig getRedisPoolConfig() {
            return new JedisPoolConfig();
        }

        @Bean
        @ConfigurationProperties(prefix = "tx.redis")
        public JedisConnectionFactory getConnectionFactory() {

            final Boolean cluster = env.getProperty("tx.redis.cluster", Boolean.class);
            if (cluster) {
                return new JedisConnectionFactory(getClusterConfiguration(),
                        getRedisPoolConfig());
            } else {
                return new JedisConnectionFactory(getRedisPoolConfig());
            }
        }


        @Bean
        @SuppressWarnings("unchecked")
        public RedisTemplate redisTemplate() {
            RedisTemplate redisTemplate = new StringRedisTemplate();
            redisTemplate.setConnectionFactory(getConnectionFactory());
            Jackson2JsonRedisSerializer jackson2JsonRedisSerializer =
                    new Jackson2JsonRedisSerializer(Object.class);
            ObjectMapper om = new ObjectMapper();
            om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
            jackson2JsonRedisSerializer.setObjectMapper(om);


            redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
            redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
            redisTemplate.afterPropertiesSet();
            return redisTemplate;
        }

        private RedisClusterConfiguration getClusterConfiguration() {
            Map<String, Object> source = Maps.newHashMap();
            source.put("spring.redis.cluster.nodes", env.getProperty("tx.redis.cluster.nodes"));
            source.put("spring.redis.cluster.max-redirects", env.getProperty("tx.redis.cluster.redirects"));
            return new RedisClusterConfiguration(new MapPropertySource("RedisClusterConfiguration", source));
        }
    }
}
