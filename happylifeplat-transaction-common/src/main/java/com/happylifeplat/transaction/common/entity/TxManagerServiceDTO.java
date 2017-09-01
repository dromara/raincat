package com.happylifeplat.transaction.common.entity;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  注册到eureka 上的tmManager
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/8/9 14:06
 * @since JDK 1.8
 */
public class TxManagerServiceDTO {

    private String appName;

    private String instanceId;

    private String homepageUrl;

    public String getAppName() {
        return appName;
    }

    public String getHomepageUrl() {
        return homepageUrl;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setHomepageUrl(String homepageUrl) {
        this.homepageUrl = homepageUrl;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ServiceDTO{");
        sb.append("appName='").append(appName).append('\'');
        sb.append(", instanceId='").append(instanceId).append('\'');
        sb.append(", homepageUrl='").append(homepageUrl).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
