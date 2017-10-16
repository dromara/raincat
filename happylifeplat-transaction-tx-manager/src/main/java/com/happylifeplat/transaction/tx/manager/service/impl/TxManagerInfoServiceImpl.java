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
package com.happylifeplat.transaction.tx.manager.service.impl;

import com.happylifeplat.transaction.common.entity.TxManagerServer;
import com.happylifeplat.transaction.common.entity.TxManagerServiceDTO;
import com.happylifeplat.transaction.tx.manager.config.NettyConfig;
import com.happylifeplat.transaction.tx.manager.entity.TxManagerInfo;
import com.happylifeplat.transaction.tx.manager.eureka.DiscoveryService;
import com.happylifeplat.transaction.tx.manager.service.TxManagerInfoService;
import com.happylifeplat.transaction.tx.manager.socket.SocketManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.eureka.EurekaServerContextHolder;
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
 * @author xiaoyu
 */
@Service("txManagerInfoService")
public class TxManagerInfoServiceImpl implements TxManagerInfoService {


    @Autowired(required = false)
    private DiscoveryService discoveryService;

    @Autowired
    private NettyConfig nettyConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${redisSaveMaxTime}")
    private int redisSaveMaxTime;

    @Value("${transactionWaitMaxTime}")
    private int transactionWaitMaxTime;


    /**
     * 业务端获取TxManager信息
     *
     * @return TxManagerServer
     */
    @Override
    public TxManagerServer findTxManagerServer() {
        final List<String> eurekaService = findEurekaService();
        if (CollectionUtils.isNotEmpty(eurekaService)) {
            final List<TxManagerInfo> txManagerInfos = eurekaService.stream().map(url ->
                    restTemplate.getForObject(url + "/tx/manager/findTxManagerInfo", TxManagerInfo.class))
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(txManagerInfos)) {
                //获取连接数最多的服务  想要把所有的业务长连接，连接到同一个tm，但是又不能超过最大的连接
                final Optional<TxManagerInfo> txManagerInfoOptional =
                        txManagerInfos.stream().filter(Objects::nonNull)
                                .filter(info -> info.getNowConnection() < info.getMaxConnection())
                                .sorted(Comparator.comparingInt(TxManagerInfo::getNowConnection).reversed())
                                .findFirst();
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

    /**
     * 服务端信息
     *
     * @return TxManagerInfo
     */
    @Override
    public TxManagerInfo findTxManagerInfo() {
        TxManagerInfo txManagerInfo = new TxManagerInfo();
        //设置ip为eureka 上注册的TxManager ip
        String ip = EurekaServerContextHolder.getInstance().getServerContext().getApplicationInfoManager().getEurekaInstanceConfig().getIpAddress();
        txManagerInfo.setIp(ip);
        txManagerInfo.setPort(nettyConfig.getPort());
        txManagerInfo.setMaxConnection(SocketManager.getInstance().getMaxConnection());
        txManagerInfo.setNowConnection(SocketManager.getInstance().getNowConnection());
        txManagerInfo.setTransactionWaitMaxTime(transactionWaitMaxTime);
        txManagerInfo.setRedisSaveMaxTime(redisSaveMaxTime);
        txManagerInfo.setClusterInfoList(findEurekaService());
        return txManagerInfo;
    }

    /**
     * 获取eureka上的注册服务
     *
     * @return List<TxManagerServiceDTO>
     */
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


    /**
     * 返回在eureka上注册的服务Url
     *
     * @return List<String>
     */
    private List<String> findEurekaService() {
        final List<InstanceInfo> configServiceInstances = discoveryService.getConfigServiceInstances();
        return configServiceInstances.stream().map(InstanceInfo::getHomePageUrl).collect(Collectors.toList());


    }
}
