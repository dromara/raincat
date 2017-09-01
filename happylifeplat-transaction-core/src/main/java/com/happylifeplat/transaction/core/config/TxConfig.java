package com.happylifeplat.transaction.core.config;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * TxTransaction 事务基本信息配置类
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/5/27 15:31
 * @since JDK 1.8
 */
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
    private int  heartTime =10;


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
    private boolean compensation = false;

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
    private int compensationRecoverTime=60;


    /**
     * 更新tmInfo 的时间间隔
     */
    private int refreshInterval=60;


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


    public String getSerializer() {
        return serializer;
    }

    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }

    public int getCompensationQueueMax() {
        return compensationQueueMax;
    }

    public void setCompensationQueueMax(int compensationQueueMax) {
        this.compensationQueueMax = compensationQueueMax;
    }

    public int getCompensationThreadMax() {
        return compensationThreadMax;
    }

    public void setCompensationThreadMax(int compensationThreadMax) {
        this.compensationThreadMax = compensationThreadMax;
    }

    public boolean getCompensation() {
        return compensation;
    }

    public void setCompensation(boolean compensation) {
        this.compensation = compensation;
    }

    public String getCompensationCacheType() {
        return compensationCacheType;
    }

    public void setCompensationCacheType(String compensationCacheType) {
        this.compensationCacheType = compensationCacheType;
    }

    public String getTxManagerUrl() {
        return txManagerUrl;
    }

    public void setTxManagerUrl(String txManagerUrl) {
        this.txManagerUrl = txManagerUrl;
    }

    public TxDbConfig getTxDbConfig() {
        return txDbConfig;
    }

    public void setTxDbConfig(TxDbConfig txDbConfig) {
        this.txDbConfig = txDbConfig;
    }

    public TxMongoConfig getTxMongoConfig() {
        return txMongoConfig;
    }

    public void setTxMongoConfig(TxMongoConfig txMongoConfig) {
        this.txMongoConfig = txMongoConfig;
    }

    public TxRedisConfig getTxRedisConfig() {
        return txRedisConfig;
    }

    public void setTxRedisConfig(TxRedisConfig txRedisConfig) {
        this.txRedisConfig = txRedisConfig;
    }

    public String getRejectPolicy() {
        return rejectPolicy;
    }

    public void setRejectPolicy(String rejectPolicy) {
        this.rejectPolicy = rejectPolicy;
    }

    public String getBlockingQueueType() {
        return blockingQueueType;
    }

    public void setBlockingQueueType(String blockingQueueType) {
        this.blockingQueueType = blockingQueueType;
    }

    public int getTransactionThreadMax() {
        return transactionThreadMax;
    }

    public void setTransactionThreadMax(int transactionThreadMax) {
        this.transactionThreadMax = transactionThreadMax;
    }

    public String getNettySerializer() {
        return nettySerializer;
    }

    public void setNettySerializer(String nettySerializer) {
        this.nettySerializer = nettySerializer;
    }

    public TxFileConfig getTxFileConfig() {
        return txFileConfig;
    }

    public void setTxFileConfig(TxFileConfig txFileConfig) {
        this.txFileConfig = txFileConfig;
    }

    public TxZookeeperConfig getTxZookeeperConfig() {
        return txZookeeperConfig;
    }

    public void setTxZookeeperConfig(TxZookeeperConfig txZookeeperConfig) {
        this.txZookeeperConfig = txZookeeperConfig;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    public int getNettyThreadMax() {
        return nettyThreadMax;
    }

    public void setNettyThreadMax(int nettyThreadMax) {
        this.nettyThreadMax = nettyThreadMax;
    }

    public int getHeartTime() {
        return heartTime;
    }

    public void setHeartTime(int heartTime) {
        this.heartTime = heartTime;
    }

    public int getCompensationRecoverTime() {
        return compensationRecoverTime;
    }

    public void setCompensationRecoverTime(int compensationRecoverTime) {
        this.compensationRecoverTime = compensationRecoverTime;
    }

    public int getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }
}
