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

package org.dromara.raincat.admin.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dromara.raincat.admin.interceptor.AuthInterceptor;
import org.dromara.raincat.common.enums.SerializeProtocolEnum;
import org.dromara.raincat.common.holder.ServiceBootstrap;
import org.dromara.raincat.common.serializer.KryoSerializer;
import org.dromara.raincat.common.serializer.ObjectSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

/**
 * AdminConfiguration.
 *
 * @author xiaoyu(Myth)
 */
@Configuration
@SuppressWarnings("all")
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

    @Bean
    @SuppressWarnings("unchecked")
    public RedisTemplate redisTemplate(@Qualifier("redisConnectionFactory")
                                               RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
