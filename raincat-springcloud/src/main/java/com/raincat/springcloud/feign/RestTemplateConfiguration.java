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
package com.raincat.springcloud.feign;


import com.raincat.common.constant.CommonConstant;
import com.raincat.core.concurrent.threadlocal.TxTransactionLocal;
import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author xiaoyu
 */
@Configuration
public class RestTemplateConfiguration {
/*
    @Bean
    @Scope("prototype")
    public Feign.Builder feignBuilder() {
        return Feign.builder().requestInterceptor(new RestTemplateInterceptor());
    }*/

    @Bean("txRequestInterceptor")
    public RequestInterceptor txRequestInterceptor() {

        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                template.header(CommonConstant.TX_TRANSACTION_GROUP, TxTransactionLocal.getInstance().getTxGroupId());

            }
        };
    }


}
