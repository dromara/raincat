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

package com.raincat.core.compensation.impl;

import com.raincat.common.bean.TransactionInvocation;
import com.raincat.common.bean.TransactionRecover;
import com.raincat.common.config.TxConfig;
import com.raincat.common.constant.CommonConstant;
import com.raincat.common.enums.CompensationOperationTypeEnum;
import com.raincat.common.enums.TransactionStatusEnum;
import com.raincat.common.holder.LogUtil;
import com.raincat.common.netty.bean.TxTransactionGroup;
import com.raincat.common.netty.bean.TxTransactionItem;
import com.raincat.core.compensation.TxCompensationService;
import com.raincat.core.concurrent.threadlocal.CompensationLocal;
import com.raincat.core.concurrent.threadpool.TxTransactionThreadFactory;
import com.raincat.core.helper.SpringBeanUtils;
import com.raincat.core.service.RpcApplicationService;
import com.raincat.core.service.TxManagerMessageService;
import com.raincat.core.spi.TransactionRecoverRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * TxCompensationServiceImpl.
 *
 * @author xiaoyu
 */
@Service
public class TxCompensationServiceImpl implements TxCompensationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxCompensationServiceImpl.class);

    private TransactionRecoverRepository transactionRecoverRepository;

    private final RpcApplicationService rpcApplicationService;

    private TxConfig txConfig;

    private final TxManagerMessageService txManagerMessageService;

    private ScheduledExecutorService scheduledExecutorService;


    @Autowired(required = false)
    public TxCompensationServiceImpl(final RpcApplicationService rpcApplicationService,
                                     final TxManagerMessageService txManagerMessageService) {
        this.rpcApplicationService = rpcApplicationService;
        this.txManagerMessageService = txManagerMessageService;
        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(1,
                TxTransactionThreadFactory.create("CompensationService", true));
    }

    @Override
    public void start(final TxConfig txConfig) throws Exception {
        this.txConfig = txConfig;
        if (txConfig.getCompensation()) {
            final String modelName = rpcApplicationService.findModelName();
            transactionRecoverRepository = SpringBeanUtils.getInstance().getBean(TransactionRecoverRepository.class);
            transactionRecoverRepository.init(modelName, txConfig);
            //执行定时补偿
            compensate();
        }
    }

    @Override
    public String save(final TransactionRecover transactionRecover) {
        final int rows = transactionRecoverRepository.create(transactionRecover);
        if (rows > 0) {
            return transactionRecover.getId();
        }
        return null;

    }

    @Override
    public boolean remove(final String id) {
        final int rows = transactionRecoverRepository.remove(id);
        return rows > 0;
    }

    @Override
    public void update(final TransactionRecover transactionRecover) {
        transactionRecoverRepository.update(transactionRecover);
    }

    @Override
    public void compensation(final TransactionRecover transactionRecover) {
        compensatoryTransfer(transactionRecover);
    }

    @SuppressWarnings("unchecked")
    private void compensatoryTransfer(final TransactionRecover transactionRecover) {
        if (Objects.nonNull(transactionRecover)) {
            final TransactionInvocation transactionInvocation = transactionRecover.getTransactionInvocation();
            if (Objects.nonNull(transactionInvocation)) {
                final Class clazz = transactionInvocation.getTargetClazz();
                final String method = transactionInvocation.getMethod();
                final Object[] argumentValues = transactionInvocation.getArgumentValues();
                final Class[] argumentTypes = transactionInvocation.getArgumentTypes();
                final Object bean = SpringBeanUtils.getInstance().getBean(clazz);
                try {
                    CompensationLocal.getInstance().setCompensationId(CommonConstant.COMPENSATE_ID);
                    MethodUtils.invokeMethod(bean, method, argumentValues, argumentTypes);
                    //通知tm自身已经完成提交 //删除本地信息
                    final Boolean success = txManagerMessageService.completeCommitTxTransaction(transactionRecover.getGroupId(),
                            transactionRecover.getTaskId(), TransactionStatusEnum.COMMIT.getCode());
                    if (success) {
                        transactionRecoverRepository.remove(transactionRecover.getId());
                    }

                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    LogUtil.error(LOGGER, "补偿方法反射调用失败！{}", e::getMessage);
                }

            }
        }

    }

    private void compensate() {
        scheduledExecutorService
                .scheduleAtFixedRate(() -> {
                    LogUtil.debug(LOGGER, "compensate recover execute delayTime:{}", () -> txConfig.getCompensationRecoverTime());
                    final List<TransactionRecover> transactionRecovers =
                            transactionRecoverRepository.listAllByDelay(acquireData());
                    if (CollectionUtils.isNotEmpty(transactionRecovers)) {
                        for (TransactionRecover transactionRecover : transactionRecovers) {
                            if (transactionRecover.getRetriedCount() > txConfig.getRetryMax()) {
                                LogUtil.error(LOGGER, "此事务超过了最大重试次数，不再进行重试：{}", () -> transactionRecover.getTransactionInvocation().getTargetClazz().getName()
                                        + ":" + transactionRecover.getTransactionInvocation().getMethod()
                                        + "事务组id：" + transactionRecover.getGroupId());
                                continue;
                            }
                            try {
                               /* //判断任务是否执行完成
                                if (CommonConstant.TX_TRANSACTION_COMPLETE_FLAG_OK.equals(transactionRecover.getCompleteFlag())) {
                                    continue;
                                }*/
                                transactionRecover.setOperation(CompensationOperationTypeEnum.COMPENSATION.getCode());
                                final int update = transactionRecoverRepository.update(transactionRecover);
                                if (update > 0) {
                                    final TxTransactionGroup byTxGroupId = txManagerMessageService
                                            .findByTxGroupId(transactionRecover.getGroupId());
                                    if (Objects.nonNull(byTxGroupId) && CollectionUtils.isNotEmpty(byTxGroupId.getItemList())) {
                                        final Optional<TxTransactionItem> any = byTxGroupId.getItemList().stream()
                                                .filter(item -> Objects.equals(item.getTaskKey(), transactionRecover.getGroupId()))
                                                .findAny();
                                        if (any.isPresent()) {
                                            final int status = any.get().getStatus();
                                            //如果整个事务组状态是提交的
                                            if (TransactionStatusEnum.COMMIT.getCode() == status) {
                                                final Optional<TxTransactionItem> txTransactionItem = byTxGroupId.getItemList().stream()
                                                        .filter(item -> Objects.equals(item.getTaskKey(), transactionRecover.getTaskId()))
                                                        .findAny();
                                                if (txTransactionItem.isPresent()) {
                                                    final TxTransactionItem item = txTransactionItem.get();
                                                    //自己的状态不是提交，那么就进行补偿
                                                    if (item.getStatus() != TransactionStatusEnum.COMMIT.getCode()) {
                                                        this.compensatoryTransfer(transactionRecover);
                                                    }else if(item.getStatus() == TransactionStatusEnum.COMMIT.getCode()){
                                                        remove(transactionRecover.getId());
                                                    }
                                                }
                                            } else {
                                                remove(transactionRecover.getId());
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                LogUtil.error(LOGGER, "执行事务补偿异常:{}", e::getMessage);
                            }

                        }
                    }
                }, 30, txConfig.getCompensationRecoverTime(), TimeUnit.SECONDS);

    }

    private Date acquireData() {
        return new Date(LocalDateTime.now()
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                - (txConfig.getRecoverDelayTime() * 1000));
    }

}
