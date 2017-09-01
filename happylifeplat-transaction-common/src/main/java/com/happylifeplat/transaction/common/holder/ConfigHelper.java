package com.happylifeplat.transaction.common.holder;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  获取配置文件工具类
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/5/27 11:56
 * @since JDK 1.8
 */
public class ConfigHelper {


    private PropertiesConfiguration propertiesConfiguration = null;

    public String getStringValue(String key) {
        return propertiesConfiguration.getString(key);
    }

    public void setProperty(String key, Object val) {
        propertiesConfiguration.setProperty(key, val);
        try {
            propertiesConfiguration.save();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public int getIntValue(String key) {
        return propertiesConfiguration.getInt(key);
    }

    public float getFloatValue(String key) {
        return propertiesConfiguration.getFloat(key);
    }

    public ConfigHelper(String propertyPath) {
        try {
            propertiesConfiguration = new PropertiesConfiguration(propertyPath);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Please configure check  file: " + propertyPath);
        }
    }

}
