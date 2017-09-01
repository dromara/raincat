package com.happylifeplat.transaction.core.bootstrap;


import com.happylifeplat.transaction.common.holder.LogUtil;
import com.happylifeplat.transaction.core.config.TxConfig;
import com.happylifeplat.transaction.core.service.InitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * 初始化类
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/12 16:54
 * @since JDK 1.8
 */
@Component
public class TxTransactionInitialize {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TxTransactionInitialize.class);

    private final InitService initService;

    @Autowired
    public TxTransactionInitialize(InitService initService) {
        this.initService = initService;
    }

    /**
     * 初始化服务
     */
    public void init(TxConfig txConfig) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> LOGGER.error("系统关闭")));
        try {
            initService.initialization(txConfig);
        } catch (RuntimeException ex) {
            LogUtil.error(LOGGER, "初始化异常:{}", ex::getMessage);
            System.exit(1);//非正常关闭
        }
    }


}
