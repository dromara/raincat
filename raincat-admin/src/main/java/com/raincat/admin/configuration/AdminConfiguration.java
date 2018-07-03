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

package com.raincat.admin.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.raincat.admin.interceptor.AuthInterceptor;
import com.raincat.common.enums.SerializeProtocolEnum;
import com.raincat.common.holder.ServiceBootstrap;
import com.raincat.common.serializer.KryoSerializer;
import com.raincat.common.serializer.ObjectSerializer;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

/**
 * AdminConfiguration.
 * @author xiaoyu(Myth)
 */
@Configuration
public class AdminConfiguration {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addInterceptors(final InterceptorRegistry registry) {
                registry.addInterceptor(new AuthInterceptor()).addPathPatterns("/**");
            }
        };
    }

    @Configuration
    static class SerializerConfiguration {

        private final Environment env;

        @Autowired
        SerializerConfiguration(final Environment env) {
            this.env = env;
        }

        @Bean
        public ObjectSerializer objectSerializer() {

            final SerializeProtocolEnum serializeProtocolEnum =
                    SerializeProtocolEnum.acquireSerializeProtocol(env.getProperty("recover.serializer.support"));
            final ServiceLoader<ObjectSerializer> objectSerializers =
                    ServiceBootstrap.loadAll(ObjectSerializer.class);
            return StreamSupport.stream(objectSerializers.spliterator(), false)
                    .filter(objectSerializer ->
                            Objects.equals(objectSerializer.getScheme(),
                                    serializeProtocolEnum.getSerializeProtocol())).findFirst().orElse(new KryoSerializer());
        }

    }

    @Configuration
    static class RedisConfiguration {

        private final Environment env;

        @Autowired
        RedisConfiguration(final Environment env) {
            this.env = env;
        }

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
        @ConfigurationProperties(prefix = "tx.redis")
        public JedisPoolConfig getRedisPoolConfig() {
            return new JedisPoolConfig();
        }

        @Bean
        @ConfigurationProperties(prefix = "tx.redis")
        public JedisConnectionFactory getConnectionFactory() {
            final Boolean cluster = env.getProperty("tx.redis.cluster", Boolean.class);
            if (cluster) {
                return new JedisConnectionFactory(getClusterConfiguration(), getRedisPoolConfig());
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
