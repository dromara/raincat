
package com.happylifeplat.transaction.tx.manager.config;


/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * Netty容器配置
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/5/11 18:02
 * @since JDK 1.8
 */
public class NettyConfig {

    /**
     * 启动服务端口
     */
    private int port;

    /**
     * 最大线程数
     */
    private int maxThreads=Runtime.getRuntime().availableProcessors()<<2;


    /**
     * 客户端与服务端链接数
     */
    private int maxConnection=50;

    /**
     * 序列化方式
     */
    private String serialize;

    /**
     * 与客户端通信最大延迟时间，超过该时间就会自动唤醒线程,返回失败  单位：秒）
     */
    private int delayTime;

    /**
     * 与客户端保持通讯的心跳时间（单位：秒）
     */
    private int heartTime;


    public int getMaxConnection() {
        return maxConnection;
    }

    public void setMaxConnection(int maxConnection) {
        this.maxConnection = maxConnection;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public String getSerialize() {
        return serialize;
    }

    public void setSerialize(String serialize) {
        this.serialize = serialize;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    public int getHeartTime() {
        return heartTime;
    }

    public void setHeartTime(int heartTime) {
        this.heartTime = heartTime;
    }

    @Override
    public String toString() {
        return "NettyConfig{" +
                "port=" + port +
                ", maxThreads=" + maxThreads +
                ", maxConnection=" + maxConnection +
                ", serialize='" + serialize + '\'' +
                ", delayTime=" + delayTime +
                ", heartTime=" + heartTime +
                '}';
    }
}

