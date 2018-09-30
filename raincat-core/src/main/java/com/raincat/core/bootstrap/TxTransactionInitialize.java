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

package com.raincat.core.bootstrap;

import com.raincat.common.holder.LogUtil;
import com.raincat.common.config.TxConfig;
import com.raincat.core.service.InitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * TxTransactionInitialize.
 * @author xiaoyu
 */
@Component
public class TxTransactionInitialize {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TxTransactionInitialize.class);

    private final InitService initService;

    @Autowired
    public TxTransactionInitialize(InitService initService) {
        this.initService = initService;
    }

    /**
     * inti service.
     */
    public void init(TxConfig txConfig) {
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> LOGGER.error("raincat init error")));
        try {
            initService.initialization(txConfig);
        } catch (RuntimeException ex) {
            LogUtil.error(LOGGER, "raincat init exception:{}", ex::getMessage);
            //非正常关闭
            System.exit(1);
        }
    }


}
