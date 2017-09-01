package com.happylifeplat.transaction.tx.manager.task;

import com.happylifeplat.transaction.tx.manager.service.TxManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * 清除redis上 已经完成的事务组
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/8/2 11:33
 * @since JDK 1.8
 */
@Component
public class RedisCleanTask {

    private final TxManagerService txManagerService;


    @Autowired
    public RedisCleanTask(TxManagerService txManagerService) {
        this.txManagerService = txManagerService;
    }


    /**
     * 清除完全提交的事务组信息，每隔5分钟执行一次
     *
     * @throws InterruptedException
     */
    @Scheduled(fixedDelay = 1000*300)
    public void removeCommitTxGroup() throws InterruptedException {
        txManagerService.removeCommitTxGroup();

    }


    /**
     * 清除完全提交的事务组信息，每隔10分钟执行一次
     *
     * @throws InterruptedException
     */
    @Scheduled(fixedDelay = 1000*600)
    public void removeRollBackTxGroup() throws InterruptedException {
        txManagerService.removeRollBackTxGroup();
    }


}
