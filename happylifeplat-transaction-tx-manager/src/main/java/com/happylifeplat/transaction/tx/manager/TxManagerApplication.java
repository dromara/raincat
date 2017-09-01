package com.happylifeplat.transaction.tx.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * spring  boot启动类
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/26 11:10
 * @since JDK 1.8
 */
@SpringBootApplication
@EnableEurekaServer
@EnableScheduling
public class TxManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TxManagerApplication.class, args);
    }

}
