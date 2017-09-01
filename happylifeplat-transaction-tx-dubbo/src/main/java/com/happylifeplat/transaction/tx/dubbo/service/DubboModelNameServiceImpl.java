package com.happylifeplat.transaction.tx.dubbo.service;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.happylifeplat.transaction.core.service.ModelNameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  获取模块名称
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/19 14:30
 * @since JDK 1.8
 */
@Service
public class DubboModelNameServiceImpl implements ModelNameService {


    /**
     * dubbo ApplicationConfig
     */
    @Autowired(required = false)
    private ApplicationConfig applicationConfig;

    @Override
    public String findModelName() {
        return applicationConfig.getName();
    }
}
