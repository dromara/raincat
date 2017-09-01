package com.happylifeplat.transaction.tx.manager.spring;

import com.happylifeplat.transaction.tx.manager.netty.NettyService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/14 17:07
 * @since JDK 1.8
 */
@Component
public class TxManagerBootstrap implements ApplicationContextAware {


    private final NettyService nettyService;

    @Autowired
    public TxManagerBootstrap(NettyService nettyService) {
        this.nettyService = nettyService;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
            nettyService.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
