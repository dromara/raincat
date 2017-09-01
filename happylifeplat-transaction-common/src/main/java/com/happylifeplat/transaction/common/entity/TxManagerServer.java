package com.happylifeplat.transaction.common.entity;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  txManager
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/13 19:32
 * @since JDK 1.8
 */
public class TxManagerServer {

    /**
     * TxManagerServer host
     */
    private String host;

    /**
     * TxManagerServer port
     */
    private Integer port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "TxManagerServer{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
