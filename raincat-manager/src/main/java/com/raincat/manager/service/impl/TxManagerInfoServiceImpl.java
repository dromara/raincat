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

package com.raincat.manager.service.impl;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.eureka.EurekaServerContextHolder;
import com.raincat.common.entity.TxManagerServer;
import com.raincat.common.entity.TxManagerServiceDTO;
import com.raincat.manager.config.NettyConfig;
import com.raincat.manager.entity.TxManagerInfo;
import com.raincat.manager.eureka.DiscoveryService;
import com.raincat.manager.service.TxManagerInfoService;
import com.raincat.manager.socket.SocketManager;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TxManagerInfoServiceImpl.
 * @author xiaoyu
 */
@Service("txManagerInfoService")
public class TxManagerInfoServiceImpl implements TxManagerInfoService {

    private final DiscoveryService discoveryService;

    private final NettyConfig nettyConfig;

    private final RestTemplate restTemplate;

    @Value("${redisSaveMaxTime}")
    private int redisSaveMaxTime;

    @Value("${transactionWaitMaxTime}")
    private int transactionWaitMaxTime;

    @Autowired(required = false)
    public TxManagerInfoServiceImpl(DiscoveryService discoveryService, NettyConfig nettyConfig, RestTemplate restTemplate) {
        this.discoveryService = discoveryService;
        this.nettyConfig = nettyConfig;
        this.restTemplate = restTemplate;
    }

    @Override
    public TxManagerServer findTxManagerServer() {
        final List<String> eurekaService = findEurekaService();
        if (CollectionUtils.isNotEmpty(eurekaService)) {
            final List<TxManagerInfo> txManagerList = eurekaService.stream().map(url ->
                    restTemplate.getForObject(url + "/tx/manager/findTxManagerInfo", TxManagerInfo.class))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(txManagerList)) {
                //获取连接数最多的服务  想要把所有的业务长连接，连接到同一个tm，但是又不能超过最大的连接
                final Optional<TxManagerInfo> txManagerInfoOptional =
                        txManagerList.stream().filter(Objects::nonNull)
                                .filter(info -> info.getNowConnection() < info.getMaxConnection())
                                .max(Comparator.comparingInt(TxManagerInfo::getNowConnection));
                if (txManagerInfoOptional.isPresent()) {
                    final TxManagerInfo txManagerInfo = txManagerInfoOptional.get();
                    TxManagerServer txManagerServer = new TxManagerServer();
                    txManagerServer.setHost(txManagerInfo.getIp());
                    txManagerServer.setPort(txManagerInfo.getPort());
                    return txManagerServer;
                }
            }
        }
        return null;
    }

    @Override
    public TxManagerInfo findTxManagerInfo() {
        TxManagerInfo txManagerInfo = new TxManagerInfo();
        //设置ip为eureka 上注册的TxManager ip
        String ip = EurekaServerContextHolder.getInstance()
                .getServerContext().getApplicationInfoManager()
                .getEurekaInstanceConfig().getIpAddress();
        txManagerInfo.setIp(ip);
        txManagerInfo.setPort(nettyConfig.getPort());
        txManagerInfo.setMaxConnection(SocketManager.getInstance().getMaxConnection());
        txManagerInfo.setNowConnection(SocketManager.getInstance().getNowConnection());
        txManagerInfo.setTransactionWaitMaxTime(transactionWaitMaxTime);
        txManagerInfo.setRedisSaveMaxTime(redisSaveMaxTime);
        txManagerInfo.setClusterInfoList(findEurekaService());
        return txManagerInfo;
    }

    @Override
    public List<TxManagerServiceDTO> loadTxManagerService() {
        final List<InstanceInfo> instanceInfoList = discoveryService.getConfigServiceInstances();
        return instanceInfoList.stream().map(instanceInfo -> {
            TxManagerServiceDTO dto = new TxManagerServiceDTO();
            dto.setAppName(instanceInfo.getAppName());
            dto.setInstanceId(instanceInfo.getInstanceId());
            dto.setHomepageUrl(instanceInfo.getHomePageUrl());
            return dto;
        }).collect(Collectors.toList());
    }

    private List<String> findEurekaService() {
        final List<InstanceInfo> configServiceInstances =
                discoveryService.getConfigServiceInstances();
        return configServiceInstances.stream()
                .map(InstanceInfo::getHomePageUrl)
                .collect(Collectors.toList());
    }
}
