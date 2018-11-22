package org.dromara.raincat.admin.service.apps.impl;

import org.dromara.raincat.admin.service.apps.AcceptApplicationNameService;
import org.dromara.raincat.admin.service.apps.enums.AcceptApplicationNameEnum;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chaocoffee
 * @date 2018/7/6
 * @description
 */
@Service
public class AcceptDefaultApplicationNameServiceImpl implements AcceptApplicationNameService {

    @Override
    public <T> List<T> acceptAppNameList(List<T> apps) {
        return apps;
    }

    @Override
    public AcceptApplicationNameEnum code() {
        return AcceptApplicationNameEnum.UNACCEPT;
    }
}
