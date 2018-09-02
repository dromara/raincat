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

package com.raincat.common.config;

import com.raincat.common.enums.CompensationCacheTypeEnum;
import com.raincat.common.enums.SerializeProtocolEnum;
import lombok.Data;

/**
 * TxConfig.
 * @author xiaoyu
 */
@Data
public class TxConfig {

    private String repositorySuffix;

    /**
     * 提供不同的序列化对象. {@linkplain SerializeProtocolEnum}
     */
    private String serializer = "kryo";

    /**
     * netty 传输的序列化协议.
     */
    private String nettySerializer = "kryo";

    /**
     * 延迟时间.
     */
    private int delayTime = 30;

    /**
     * netty 工作线程大小.
     */
    private int nettyThreadMax = Runtime.getRuntime().availableProcessors() << 1;

    /**
     * 心跳时间 默认10秒.
     */
    private int heartTime = 10;


    /**
     * txManagerUrl服务地址.
     */
    private String txManagerUrl;

    /**
     * 是否需要补偿.
     */
    private Boolean compensation = false;

    /**
     * 补偿存储类型. {@linkplain CompensationCacheTypeEnum}
     */
    private String compensationCacheType;

    /**
     * 补偿恢复时间 单位秒.
     */
    private int compensationRecoverTime = 60;

    /**
     * 更新tmInfo 的时间间隔.
     */
    private int refreshInterval = 60;

    /**
     * 最大重试次数.
     */
    private int retryMax = 10;

    /**
     * 事务恢复间隔时间 单位秒（注意 此时间表示本地事务创建的时间多少秒以后才会执行）.
     */
    private int recoverDelayTime = 60;


    /**
     * disruptor bufferSize.
     */
    private int bufferSize = 1024;


    /**
     * db存储方式时候 数据库配置信息.
     */
    private TxDbConfig txDbConfig;

    /**
     * mongo存储方式时候的 mongo配置信息.
     */
    private TxMongoConfig txMongoConfig;

    /**
     * redis存储方式时候的 redis配置信息.
     */
    private TxRedisConfig txRedisConfig;

    /**
     * 文件存储配置.
     */
    private TxFileConfig txFileConfig;

    /**
     * zookeeper 存储的配置.
     */
    private TxZookeeperConfig txZookeeperConfig;


    public TxConfig(final Builder builder) {
        builder(builder);
    }

    public TxConfig() {
    }

    public static Builder create() {
        return new Builder();
    }

    public void builder(final Builder builder) {
        this.serializer = builder.serializer;
        this.nettySerializer = builder.nettySerializer;
        this.delayTime=builder.delayTime;
        this.nettyThreadMax=builder.nettyThreadMax;
        this.heartTime=builder.heartTime;
        this.txManagerUrl=builder.txManagerUrl;
        this.repositorySuffix = builder.repositorySuffix;
        this.compensationCacheType = builder.compensationCacheType;
        this.compensation = builder.compensation;
        this.retryMax = builder.retryMax;
        this.recoverDelayTime = builder.recoverDelayTime;
        this.refreshInterval=builder.refreshInterval;
        this.bufferSize = builder.bufferSize;
        this.txDbConfig = builder.txDbConfig;
        this.txMongoConfig = builder.txMongoConfig;
        this.txRedisConfig = builder.txRedisConfig;
        this.txZookeeperConfig = builder.txZookeeperConfig;
        this.txFileConfig = builder.txFileConfig;
    }

    public static class Builder {

        private String repositorySuffix;

        private String serializer = "kryo";

        private String nettySerializer;

        /**
         * 延迟时间.
         */
        private int delayTime = 30;

        /**
         * netty 工作线程大小.
         */
        private int nettyThreadMax = Runtime.getRuntime().availableProcessors() << 1;

        /**
         * 心跳时间 默认10秒.
         */
        private int heartTime = 10;

        private String txManagerUrl;

        private String compensationCacheType = "db";

        private Boolean compensation = false;

        private int refreshInterval;

        private int retryMax = 3;

        private int recoverDelayTime = 60;

        private int bufferSize = 1024;

        private TxDbConfig txDbConfig;

        private TxMongoConfig txMongoConfig;

        private TxRedisConfig txRedisConfig;

        private TxZookeeperConfig txZookeeperConfig;

        private TxFileConfig txFileConfig;

        public Builder setRepositorySuffix(String repositorySuffix) {
            this.repositorySuffix = repositorySuffix;
            return this;
        }

        public Builder setSerializer(String serializer) {
            this.serializer = serializer;
            return this;
        }

        public Builder setNettySerializer(String nettySerializer) {
            this.nettySerializer = nettySerializer;
            return this;
        }

        public Builder setDelayTime(int delayTime) {
            this.delayTime = delayTime;
            return this;
        }

        public Builder setNettyThreadMax(int nettyThreadMax) {
            this.nettyThreadMax = nettyThreadMax;
            return this;
        }

        public Builder setHeartTime(int heartTime) {
            this.heartTime = heartTime;
            return this;
        }

        public Builder setRefreshInterval(int refreshInterval) {
            this.refreshInterval = refreshInterval;
            return this;
        }

        public Builder setTxManagerUrl(String txManagerUrl) {
            this.txManagerUrl = txManagerUrl;
            return this;
        }



        public Builder setCompensationCacheType(String compensationCacheType) {
            this.compensationCacheType = compensationCacheType;
            return this;
        }

        public Builder setCompensation(Boolean compensation) {
            this.compensation = compensation;
            return this;
        }

        public Builder setRetryMax(int retryMax) {
            this.retryMax = retryMax;
            return this;
        }

        public Builder setRecoverDelayTime(int recoverDelayTime) {
            this.recoverDelayTime = recoverDelayTime;
            return this;
        }

        public Builder setBufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        public Builder setTxDbConfig(TxDbConfig txDbConfig) {
            this.txDbConfig = txDbConfig;
            return this;
        }

        public Builder setTxMongoConfig(TxMongoConfig txMongoConfig) {
            this.txMongoConfig = txMongoConfig;
            return this;
        }

        public Builder setTxRedisConfig(TxRedisConfig txRedisConfig) {
            this.txRedisConfig = txRedisConfig;
            return this;
        }

        public Builder setTxZookeeperConfig(TxZookeeperConfig txZookeeperConfig) {
            this.txZookeeperConfig = txZookeeperConfig;
            return this;
        }

        public Builder setTxFileConfig(TxFileConfig txFileConfig) {
            this.txFileConfig = txFileConfig;
            return this;
        }

        public TxConfig build() {
            return new TxConfig(this);
        }
    }


}
