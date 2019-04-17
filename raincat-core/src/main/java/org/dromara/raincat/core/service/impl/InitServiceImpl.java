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

import org.dromara.raincat.common.config.TxConfig;
import org.dromara.raincat.common.exception.TransactionRuntimeException;
import org.dromara.raincat.common.holder.extension.ExtensionLoader;
import org.dromara.raincat.common.serializer.ObjectSerializer;
import org.dromara.raincat.core.compensation.TxCompensationService;
import org.dromara.raincat.core.helper.SpringBeanUtils;
import org.dromara.raincat.core.logo.RaincatLogo;
import org.dromara.raincat.core.netty.NettyClientService;
import org.dromara.raincat.core.service.InitService;
import org.dromara.raincat.core.spi.TransactionRecoverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * tx transaction init.
 *
 * @author xiaoyu
 */
@Component
public class InitServiceImpl implements InitService {

    private final NettyClientService nettyClientService;

    private final TxCompensationService txCompensationService;


    @Autowired
    public InitServiceImpl(final NettyClientService nettyClientService,
                           final TxCompensationService txCompensationService) {
        this.nettyClientService = nettyClientService;
        this.txCompensationService = txCompensationService;
    }

    @Override
    public void initialization(final TxConfig txConfig) {
        try {
            loadSpi(txConfig);
            nettyClientService.start(txConfig);
            txCompensationService.start(txConfig);
        } catch (Exception e) {
            throw new TransactionRuntimeException("tx transaction ex:{}：" + e.getMessage());
        }
        new RaincatLogo().logo();
    }

    /**
     * load spi.
     *
     * @param txConfig {@linkplain TxConfig}
     */
    private void loadSpi(final TxConfig txConfig) {
        //spi serialize
        final ObjectSerializer serializer = ExtensionLoader.getExtensionLoader(ObjectSerializer.class)
                .getActivateExtension(txConfig.getSerializer());
        //spi repository
        final TransactionRecoverRepository repository = ExtensionLoader.getExtensionLoader(TransactionRecoverRepository.class)
                .getActivateExtension(txConfig.getCompensationCacheType());
        repository.setSerializer(serializer);
        SpringBeanUtils.getInstance().registerBean(TransactionRecoverRepository.class.getName(), repository);
    }

}
