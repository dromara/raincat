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

import static org.apache.coyote.http11.Constants.a;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * 获取TxManagerInfo服务
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/14 18:57
 * @since JDK 1.8
 */
@Service("txManagerInfoService")
public class TxManagerInfoServiceImpl implements TxManagerInfoService {


    @Autowired(required = false)
    private DiscoveryService discoveryService;

    @Autowired
    private NettyConfig nettyConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${redis_save_max_time}")
    private int redis_save_max_time;

    @Value("${transaction_wait_max_time}")
    private int transaction_wait_max_time;


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
        txManagerInfo.setTransactionWaitMaxTime(transaction_wait_max_time);
        txManagerInfo.setRedisSaveMaxTime(redis_save_max_time);
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
