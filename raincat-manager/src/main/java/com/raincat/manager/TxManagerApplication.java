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

package com.raincat.manager;

import com.raincat.manager.netty.NettyService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * TxManagerApplication.
 * @author xiaoyu
 */
@SpringBootApplication
@EnableEurekaServer
@EnableScheduling
public class TxManagerApplication {
    public static void main(String[] args) {
        final ConfigurableApplicationContext applicationContext =
                SpringApplication.run(TxManagerApplication.class, args);
        final NettyService nettyService = applicationContext.getBean(NettyService.class);
        try {
            nettyService.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
