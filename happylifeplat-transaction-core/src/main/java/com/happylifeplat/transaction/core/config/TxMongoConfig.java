package com.happylifeplat.transaction.core.config;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/5/27 16:05
 * @since JDK 1.8
 */
public class TxMongoConfig {

    /**
     * mongo数据库设置
     */
    private String mongoDbName;

    /**
     * mongo数据库URL
     */
    private String mongoDbUrl;
    /**
     * mongo数据库用户名
     */
    private String mongoUserName;

    /**
     * mongo数据库密码
     */
    private String mongoUserPwd;

    public String getMongoDbName() {
        return mongoDbName;
    }

    public void setMongoDbName(String mongoDbName) {
        this.mongoDbName = mongoDbName;
    }

    public String getMongoDbUrl() {
        return mongoDbUrl;
    }

    public void setMongoDbUrl(String mongoDbUrl) {
        this.mongoDbUrl = mongoDbUrl;
    }

    public String getMongoUserName() {
        return mongoUserName;
    }

    public void setMongoUserName(String mongoUserName) {
        this.mongoUserName = mongoUserName;
    }

    public String getMongoUserPwd() {
        return mongoUserPwd;
    }

    public void setMongoUserPwd(String mongoUserPwd) {
        this.mongoUserPwd = mongoUserPwd;
    }

    @Override
    public String toString() {
        return "MongoConfig{" +
                "mongoDbName='" + mongoDbName + '\'' +
                ", mongoDbUrl='" + mongoDbUrl + '\'' +
                ", mongoUserName='" + mongoUserName + '\'' +
                ", mongoUserPwd='" + mongoUserPwd + '\'' +
                '}';
    }
}
