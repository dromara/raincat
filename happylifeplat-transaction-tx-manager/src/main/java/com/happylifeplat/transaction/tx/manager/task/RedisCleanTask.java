/*
 *
 * Copyright 2017-2018 549477611@qq.com(xiaoyu)
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.happylifeplat.transaction.tx.manager.task;

import com.happylifeplat.transaction.tx.manager.service.TxManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author xiaoyu
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
     * @throws InterruptedException 异常
     */
    public void removeCommitTxGroup() throws InterruptedException {
        txManagerService.removeCommitTxGroup();

    }


    /**
     * 清除完全回滚的事务组信息，每隔10分钟执行一次
     *
     * @throws InterruptedException 异常
     */
    public void removeRollBackTxGroup() throws InterruptedException {
        txManagerService.removeRollBackTxGroup();
    }


}
