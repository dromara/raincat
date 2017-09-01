package com.happylifeplat.transaction.core.config;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  使用mysql存储时候，配置信息  使用spring-jdbc来操作
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/5/27 16:04
 * @since JDK 1.8
 */
public class TxDbConfig {

    /**
     * Mysql 驱动
     */
    private String driverClassName = "com.mysql.jdbc.Driver";

    /**
     * url
     */
    private String url;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
