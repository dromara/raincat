package com.happylifeplat.transaction.core.config.tcc;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/5/27 15:31
 * @since JDK 1.8
 */
public class TccConfig {

    /**
     * dubbo客户端名称
     */
    private String dubboName;
    /**
     * zookeeper地址
     */
    private String zookeeperUrl;
    /**
     * 提供不同的序列化对象
     */
    private String serializer;

    /**
     * 回滚队列大小
     */
    private int rollbackQueueMax;
    /**
     * 监听回滚队列线程数
     */
    private int rollbackThreadMax;


    /**
     * 是否采用集群模式
     */
    private boolean slaved;


    /**
     * 是否存储事务信息
     */
    private boolean cacheAbled;


    /**
     * 事务存储对象db
     */
    private String cacheDb;


    public String getDubboName() {
        return dubboName;
    }

    public void setDubboName(String dubboName) {
        this.dubboName = dubboName;
    }

    public String getZookeeperUrl() {
        return zookeeperUrl;
    }

    public void setZookeeperUrl(String zookeeperUrl) {
        this.zookeeperUrl = zookeeperUrl;
    }

    public String getSerializer() {
        return serializer;
    }

    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }

    public int getRollbackQueueMax() {
        return rollbackQueueMax;
    }

    public void setRollbackQueueMax(int rollbackQueueMax) {
        this.rollbackQueueMax = rollbackQueueMax;
    }

    public int getRollbackThreadMax() {
        return rollbackThreadMax;
    }

    public void setRollbackThreadMax(int rollbackThreadMax) {
        this.rollbackThreadMax = rollbackThreadMax;
    }

    public boolean isSlaved() {
        return slaved;
    }

    public void setSlaved(boolean slaved) {
        this.slaved = slaved;
    }

    public boolean isCacheAbled() {
        return cacheAbled;
    }

    public void setCacheAbled(boolean cacheAbled) {
        this.cacheAbled = cacheAbled;
    }

    public String getCacheDb() {
        return cacheDb;
    }

    public void setCacheDb(String cacheDb) {
        this.cacheDb = cacheDb;
    }


}
