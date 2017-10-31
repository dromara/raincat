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
package com.happylifeplat.transaction.core.bootstrap;


import com.happylifeplat.transaction.common.holder.LogUtil;
import com.happylifeplat.transaction.common.config.TxConfig;
import com.happylifeplat.transaction.core.service.InitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author xiaoyu
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
            //非正常关闭
            System.exit(1);
        }
    }


}
