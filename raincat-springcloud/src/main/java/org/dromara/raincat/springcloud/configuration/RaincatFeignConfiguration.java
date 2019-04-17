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

package org.dromara.raincat.springcloud.configuration;

import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import feign.RequestInterceptor;
import org.dromara.raincat.springcloud.feign.RaincatFeignInterceptor;
import org.dromara.raincat.springcloud.hystrix.RaincatHystrixConcurrencyStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RestTemplateConfiguration.
 *
 * @author xiaoyu
 */
@Configuration
public class RaincatFeignConfiguration {

    /**
     * Request interceptor request interceptor.
     *
     * @return the request interceptor
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RaincatFeignInterceptor();
    }

    /**
     * Hystrix concurrency strategy hystrix concurrency strategy.
     *
     * @return the hystrix concurrency strategy
     */
    @Bean
    @ConditionalOnProperty(name = "feign.hystrix.enabled")
    public HystrixConcurrencyStrategy hystrixConcurrencyStrategy() {
        return new RaincatHystrixConcurrencyStrategy();
    }

}
