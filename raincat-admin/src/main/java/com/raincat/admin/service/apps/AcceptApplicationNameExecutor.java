package com.raincat.admin.service.apps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author chaoshen
 * @date 2018/7/6
 * @description
 */
@Component
public class AcceptApplicationNameExecutor {

    @Autowired
    AcceptApplicationNameFactory acceptApplicationNameFactory;

    /**
     * 执行任务
     *
     * @param apps
     */
    public void execute(List<String> apps) {
        List<AcceptApplicationNameService> services = acceptApplicationNameFactory.getBeans();
        services.forEach(service -> service.acceptAppNameList(apps));
    }
}
