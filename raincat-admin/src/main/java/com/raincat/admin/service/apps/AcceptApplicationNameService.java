package com.raincat.admin.service.apps;

import com.raincat.admin.service.apps.enums.AcceptApplicationNameEnum;

import java.util.List;

/**
 * @author chaocoffee
 * @date 2018/7/5
 * @description 从注册中心获取参与分布式事务应用名称列表
 */
public interface AcceptApplicationNameService {

    String APP_URL = "/apps";

    /***
     * 获取注册中心所有应用列表
     * @param apps 已有的应用列表
     * @return 去重后的注册中心应用列表
     */
    <T> List<T> acceptAppNameList(List<T> apps);

    /**
     * 注册中心方式
     *
     * @return 注册中心枚举类
     */
    AcceptApplicationNameEnum code();

}
