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
package com.happylifeplat.transaction.tx.manager.eureka;

import com.happylifeplat.transaction.tx.manager.config.Constant;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaoyu
 */
@Service
public class DiscoveryService {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveryService.class);

    private final EurekaClient eurekaClient;

    @Autowired(required = false)
    public DiscoveryService(EurekaClient eurekaClient) {
        this.eurekaClient = eurekaClient;
    }

    public List<InstanceInfo> getConfigServiceInstances() {
        Application application = eurekaClient.getApplication(Constant.APPLICATION_NAME);
        if (application == null) {
            LOGGER.error("获取eureka服务失败！");
        }
        return application != null ? application.getInstances() : new ArrayList<>();
    }
}
