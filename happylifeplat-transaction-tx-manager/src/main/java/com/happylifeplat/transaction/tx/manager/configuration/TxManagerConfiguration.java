package com.happylifeplat.transaction.tx.manager.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.happylifeplat.transaction.tx.manager.config.NettyConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.JedisPoolConfig;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * TxManager配置信息
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/14 16:35
 * @since JDK 1.8
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
        @Bean
        public KeyGenerator keyGenerator() {
            return (target, method, params) -> {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            };
        }

        @Bean
        @ConfigurationProperties(prefix = "spring.redis")
        public JedisPoolConfig getRedisConfig() {
            return new JedisPoolConfig();
        }

        @Bean
        @ConfigurationProperties(prefix = "spring.redis")
        public JedisConnectionFactory getConnectionFactory() {
            JedisConnectionFactory factory = new JedisConnectionFactory();
            JedisPoolConfig config = getRedisConfig();
            factory.setPoolConfig(config);
            return factory;
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
    }
}
