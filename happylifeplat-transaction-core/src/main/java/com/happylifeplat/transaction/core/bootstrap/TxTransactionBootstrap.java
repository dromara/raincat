package com.happylifeplat.transaction.core.bootstrap;

import com.happylifeplat.transaction.common.exception.TransactionRuntimeException;
import com.happylifeplat.transaction.core.config.TxConfig;
import com.happylifeplat.transaction.core.helper.SpringBeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * TxTransaction 启动类
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/12 16:54
 * @since JDK 1.8
 */
@Component
public class TxTransactionBootstrap extends TxConfig implements ApplicationContextAware {

    /**
     * logger
     *//*
    private static final Logger LOGGER = LoggerFactory.getLogger(TxTransactionBootstrap.class);


    private static final String SCAN_PACKAGE = "com.happylifeplat.transaction.*";*/


    private ConfigurableApplicationContext cfgContext;

    /**
     * 初始化实体
     */
    private final TxTransactionInitialize txTransactionInitialize;

    @Autowired
    public TxTransactionBootstrap(TxTransactionInitialize txTransactionInitialize) {
        this.txTransactionInitialize = txTransactionInitialize;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        cfgContext = (ConfigurableApplicationContext) applicationContext;
        SpringBeanUtils.getInstance().setCfgContext(cfgContext);
        start(this);
    }



    private void start(TxConfig txConfig) {
        if (!checkDataConfig(txConfig)) {
            throw new TransactionRuntimeException("分布式事务配置信息不完整！");
        }
        txTransactionInitialize.init(txConfig);
    }

    private boolean checkDataConfig(TxConfig txConfig) {
        return !StringUtils.isBlank(txConfig.getTxManagerUrl());
    }
}




























