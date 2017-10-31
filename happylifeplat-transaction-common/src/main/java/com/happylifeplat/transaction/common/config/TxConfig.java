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
package com.happylifeplat.transaction.common.config;

import lombok.Data;

/**
 * @author xiaoyu
 */
@Data
public class TxConfig {

    /**
     * 提供不同的序列化对象 {@linkplain com.happylifeplat.transaction.common.enums.SerializeProtocolEnum}
     */
    private String serializer = "kryo";


    /**
     * netty 传输的序列化协议
     */
    private String nettySerializer = "kryo";


    /**
     * 延迟时间
     */
    private int delayTime = 30;


    /**
     * 执行事务的线程数大小
     */
    private int transactionThreadMax = Runtime.getRuntime().availableProcessors() << 1;


    /**
     * netty 工作线程大小
     */
    private int nettyThreadMax = Runtime.getRuntime().availableProcessors() << 1;

    /**
     * 心跳时间 默认10秒
     */
    private int heartTime = 10;


    /**
     * 线程池的拒绝策略 {@linkplain com.happylifeplat.transaction.common.enums.RejectedPolicyTypeEnum}
     */
    private String rejectPolicy = "Abort";

    /**
     * 线程池的队列类型 {@linkplain com.happylifeplat.transaction.common.enums.BlockingQueueTypeEnum}
     */
    private String blockingQueueType = "Linked";

    /**
     * 是否需要补偿
     */
    private Boolean compensation = false;

    /**
     * 补偿存储类型 {@linkplain com.happylifeplat.transaction.common.enums.CompensationCacheTypeEnum}
     */
    private String compensationCacheType;


    /**
     * 回滚队列大小
     */
    private int compensationQueueMax = 5000;
    /**
     * 监听回滚队列线程数
     */
    private int compensationThreadMax = Runtime.getRuntime().availableProcessors() << 1;


    /**
     * 补偿恢复时间 单位秒
     */
    private int compensationRecoverTime = 60;


    /**
     * 更新tmInfo 的时间间隔
     */
    private int refreshInterval = 60;


    /**
     * 最大重试次数
     */
    private int retryMax = 10;


    /**
     * 事务恢复间隔时间 单位秒（注意 此时间表示本地事务创建的时间多少秒以后才会执行）
     */
    private int recoverDelayTime = 60;


    /**
     * txManagerUrl服务地址
     */
    private String txManagerUrl;


    /**
     * db存储方式时候 数据库配置信息
     */
    private TxDbConfig txDbConfig;

    /**
     * mongo存储方式时候的 mongo配置信息
     */
    private TxMongoConfig txMongoConfig;


    /**
     * redis存储方式时候的 redis配置信息
     */
    private TxRedisConfig txRedisConfig;

    /**
     * 文件存储配置
     */
    private TxFileConfig txFileConfig;

    /**
     * zookeeper 存储的配置
     */
    private TxZookeeperConfig txZookeeperConfig;


}
