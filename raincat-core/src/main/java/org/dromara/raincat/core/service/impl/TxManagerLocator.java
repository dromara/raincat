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

package org.dromara.raincat.core.service.impl;

import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import org.dromara.raincat.common.config.TxConfig;
import org.dromara.raincat.common.constant.CommonConstant;
import org.dromara.raincat.common.entity.TxManagerServer;
import org.dromara.raincat.common.entity.TxManagerServiceDTO;
import org.dromara.raincat.common.holder.CollectionUtils;
import org.dromara.raincat.common.holder.LogUtil;
import org.dromara.raincat.common.holder.httpclient.OkHttpTools;
import org.dromara.raincat.core.concurrent.threadpool.TxTransactionThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * TxManagerLocator.
 *
 * @author xiaoyu
 */
public final class TxManagerLocator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxManagerLocator.class);

    private static final TxManagerLocator TX_MANAGER_LOCATOR = new TxManagerLocator();

    private TxConfig txConfig;

    private ScheduledExecutorService scheduledExecutorService;

    private AtomicReference<List<TxManagerServiceDTO>> listAtomicReference;

    private Type type;

    private TxManagerLocator() {
        List<TxManagerServiceDTO> initial = Lists.newArrayList();
        listAtomicReference = new AtomicReference<>(initial);
        type = new TypeToken<List<TxManagerServiceDTO>>() {
        }.getType();
        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(1,
                TxTransactionThreadFactory.create("TxManagerLocator", true));
    }

    public void setTxConfig(final TxConfig txConfig) {
        this.txConfig = txConfig;
    }

    public static TxManagerLocator getInstance() {
        return TX_MANAGER_LOCATOR;
    }

    /**
     * acquire tx manage info.
     *
     * @return {@linkplain TxManagerServer}
     */
    public TxManagerServer locator() {
        int maxRetries = 2;
        final List<TxManagerServiceDTO> txManagerService = getTxManagerService();
        if (CollectionUtils.isEmpty(txManagerService)) {
            return null;
        }
        for (int i = 0; i < maxRetries; i++) {
            List<TxManagerServiceDTO> randomServices = Lists.newLinkedList(txManagerService);
            Collections.shuffle(randomServices);
            for (TxManagerServiceDTO serviceDTO : randomServices) {
                String url = String.join("", serviceDTO.getHomepageUrl(), CommonConstant.TX_MANAGER_PRE, CommonConstant.FIND_SERVER);
                LOGGER.debug("Loading service from {}", url);
                try {
                    return OkHttpTools.getInstance().get(url, null, TxManagerServer.class);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    LogUtil.error(LOGGER, "loadTxManagerServer fail exception:{}", ex::getMessage);
                }
            }
        }
        return null;
    }

    private List<TxManagerServiceDTO> getTxManagerService() {
        if (listAtomicReference.get().isEmpty()) {
            updateTxManagerServices();
        }
        return listAtomicReference.get();
    }

    public void schedulePeriodicRefresh() {
        this.scheduledExecutorService
                .scheduleAtFixedRate(this::run, 0,
                        txConfig.getRefreshInterval(), TimeUnit.SECONDS);
    }

    private synchronized void updateTxManagerServices() {
        String url = assembleUrl();
        int maxRetries = 2;
        for (int i = 0; i < maxRetries; i++) {
            try {
                final List<TxManagerServiceDTO> serviceDTOList =
                        OkHttpTools.getInstance().get(url, type);
                if (CollectionUtils.isEmpty(serviceDTOList)) {
                    LogUtil.error(LOGGER, "Empty response! 请求url为:{}", () -> url);
                    continue;
                }
                listAtomicReference.set(serviceDTOList);
                return;
            } catch (Throwable ex) {
                LogUtil.error(LOGGER, "updateTxManagerServices fail exception:{}", ex::getMessage);
            }
        }
    }

    private String assembleUrl() {
        return String.join("", txConfig.getTxManagerUrl(), CommonConstant.TX_MANAGER_PRE, CommonConstant.LOAD_TX_MANAGER_SERVICE_URL);
    }

    private void run() {
        LogUtil.info(LOGGER, "refresh updateTxManagerServices delayTime:{}", () -> txConfig.getRefreshInterval());
        updateTxManagerServices();
    }
}
