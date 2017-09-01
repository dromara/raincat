package com.happylifeplat.transaction.tx.manager.config;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * TmManager ip 端口信息
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/8/8 11:50
 * @since JDK 1.8
 */
public class Address {
    private static final Address ourInstance = new Address();

    /**
     * 自身的ip
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;


    /**
     * 域名  ip：port
     */
    private String domain;



    public static Address getInstance() {
        return ourInstance;
    }

    private Address() {
    }


    public String getHost() {
        return host;
    }

    public Address setHost(String host) {
        this.host = host;
        return this;
    }
    public Integer getPort() {
        return port;
    }

    public Address setPort(Integer port) {
        this.port = port;
        return this;
    }

    public String getDomain() {
        return domain;
    }

    public Address setDomain(String domain) {
        this.domain = domain;
        return this;
    }
}
