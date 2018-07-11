package com.raincat.admin.service.apps.impl;

import com.google.common.base.Splitter;
import com.raincat.admin.service.apps.AcceptApplicationNameService;
import com.raincat.admin.service.apps.enums.AcceptApplicationNameEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chaoscoffee
 * @date 2018/7/10
 * @description
 */
@Service
public class AcceptPropertiesApplicationNameService implements AcceptApplicationNameService {

    @Value("${recover.application.list}")
    private String appNameList;

    @Override
    public <T> List<T> acceptAppNameList(List<T> apps) {
        if (StringUtils.isNotBlank(appNameList)) {
            Splitter.on(",").splitToList(appNameList).forEach(app -> apps.add((T) app));
        }
        return apps;
    }

    @Override
    public AcceptApplicationNameEnum code() {
        return AcceptApplicationNameEnum.PROPERTY;
    }
}
