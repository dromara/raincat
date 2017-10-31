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
package com.happylifeplat.transaction.core.service.impl;

import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import com.happylifeplat.transaction.common.constant.CommonConstant;
import com.happylifeplat.transaction.common.entity.TxManagerServer;
import com.happylifeplat.transaction.common.entity.TxManagerServiceDTO;
import com.happylifeplat.transaction.common.holder.LogUtil;
import com.happylifeplat.transaction.common.holder.httpclient.OkHttpTools;
import com.happylifeplat.transaction.core.concurrent.threadpool.TxTransactionThreadFactory;
import com.happylifeplat.transaction.common.config.TxConfig;
import org.apache.commons.collections.CollectionUtils;
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
 * @author xiaoyu
 */
public class TxManagerLocator {

    private static final TxManagerLocator TX_MANAGER_LOCATOR = new TxManagerLocator();

    public static TxManagerLocator getInstance() {
        return TX_MANAGER_LOCATOR;
    }

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TxManagerLocator.class);

    private TxConfig txConfig;

    private ScheduledExecutorService mExecutorservice;

    private AtomicReference<List<TxManagerServiceDTO>> mConfigservices;

    private Type mResponsetype;

    public void setTxConfig(TxConfig txConfig) {
        this.txConfig = txConfig;
    }

    private TxManagerLocator() {
        List<TxManagerServiceDTO> initial = Lists.newArrayList();
        mConfigservices = new AtomicReference<>(initial);
        mResponsetype = new TypeToken<List<TxManagerServiceDTO>>() {
        }.getType();
        this.mExecutorservice = new ScheduledThreadPoolExecutor(1,
                TxTransactionThreadFactory.create("TxManagerLocator", true));
    }


    /**
     * 获取TxManager 服务信息
     *
     * @return TxManagerServer
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
        if (mConfigservices.get().isEmpty()) {
            updateTxManagerServices();
        }
        return mConfigservices.get();
    }


    public void schedulePeriodicRefresh() {
        this.mExecutorservice.scheduleAtFixedRate(
                () -> {
                    LogUtil.info(LOGGER, "refresh updateTxManagerServices delayTime:{}", () -> txConfig.getRefreshInterval());
                    updateTxManagerServices();
                }, 0, txConfig.getRefreshInterval(),
                TimeUnit.SECONDS);
    }


    private synchronized void updateTxManagerServices() {
        String url = assembleUrl();
        int maxRetries = 2;
        for (int i = 0; i < maxRetries; i++) {
            try {
                final List<TxManagerServiceDTO> serviceDTOList =
                        OkHttpTools.getInstance().get(url, mResponsetype);
                if (CollectionUtils.isEmpty(serviceDTOList)) {
                    LogUtil.error(LOGGER, "Empty response! 请求url为:{}", () -> url);
                    continue;
                }
                mConfigservices.set(serviceDTOList);
                return;
            } catch (Throwable ex) {
                ex.printStackTrace();
                LogUtil.error(LOGGER, "updateTxManagerServices fail exception:{}", ex::getMessage);
               /* throw new TransactionRuntimeException(
                        String.format("Get config services failed from %s", url), ex);*/
            }
        }

    }

    private String assembleUrl() {
        return String.join("", txConfig.getTxManagerUrl(), CommonConstant.TX_MANAGER_PRE, CommonConstant.LOAD_TX_MANAGER_SERVICE_URL);
    }

}
