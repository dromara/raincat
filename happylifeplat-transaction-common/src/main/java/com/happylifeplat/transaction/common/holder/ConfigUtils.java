package com.happylifeplat.transaction.common.holder;


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
public class ConfigUtils {

    public static String getString(String filePath, String key) {
        ConfigHelper helper = new ConfigHelper(filePath);
        return helper.getStringValue(key);
    }

    public static int getInt(String filePath, String key) {
        ConfigHelper helper = new ConfigHelper(filePath);
        return helper.getIntValue(key);
    }

    public static void setProperty(String filePath, String key, Object val) {
        ConfigHelper helper = new ConfigHelper(filePath);
        helper.setProperty(key, val);
    }

}
