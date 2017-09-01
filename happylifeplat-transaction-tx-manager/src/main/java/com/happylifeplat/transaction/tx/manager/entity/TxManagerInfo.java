package com.happylifeplat.transaction.tx.manager.entity;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/14 17:57
 * @since JDK 1.8
 */
public class TxManagerInfo implements Serializable {



    /**
     * socket ip
     */
    private String ip;
    /**
     * socket port
     */
    private int port;

    /**
     * max connection
     */
    private int maxConnection;

    /**
     * now connection
     */
    private int nowConnection;

    /**
     * transaction_wait_max_time
     */
    private int transactionWaitMaxTime;

    /**
     * redis_save_max_time
     */
    private int redisSaveMaxTime;

    /**
     * clusterInfoList
     */
    private List<String> clusterInfoList;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMaxConnection() {
        return maxConnection;
    }

    public void setMaxConnection(int maxConnection) {
        this.maxConnection = maxConnection;
    }

    public int getNowConnection() {
        return nowConnection;
    }

    public void setNowConnection(int nowConnection) {
        this.nowConnection = nowConnection;
    }

    public int getTransactionWaitMaxTime() {
        return transactionWaitMaxTime;
    }

    public void setTransactionWaitMaxTime(int transactionWaitMaxTime) {
        this.transactionWaitMaxTime = transactionWaitMaxTime;
    }

    public int getRedisSaveMaxTime() {
        return redisSaveMaxTime;
    }

    public void setRedisSaveMaxTime(int redisSaveMaxTime) {
        this.redisSaveMaxTime = redisSaveMaxTime;
    }

    public List<String> getClusterInfoList() {
        return clusterInfoList;
    }

    public void setClusterInfoList(List<String> clusterInfoList) {
        this.clusterInfoList = clusterInfoList;
    }

    @Override
    public String toString() {
        return "TxManagerInfo{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", maxConnection=" + maxConnection +
                ", nowConnection=" + nowConnection +
                ", transactionWaitMaxTime=" + transactionWaitMaxTime +
                ", redisSaveMaxTime=" + redisSaveMaxTime +
                ", clusterInfoList=" + clusterInfoList +
                '}';
    }
}
