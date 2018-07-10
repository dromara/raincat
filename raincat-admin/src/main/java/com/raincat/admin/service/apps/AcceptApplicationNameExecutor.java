package com.raincat.admin.service.apps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author chaoscoffee
 * @date 2018/7/6
 * @description
 */
@Component
public class AcceptApplicationNameExecutor {

    @Autowired
    private AcceptApplicationNameFactory acceptApplicationNameFactory;

    /**
     * 获取应用列表入口
     *
     * @param apps
     */
    public void execute(List<String> apps) {
        List<AcceptApplicationNameService> services = acceptApplicationNameFactory.getBeans();
        services.forEach(service -> service.acceptAppNameList(apps));
    }
}
