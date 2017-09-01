package com.happylifeplat.transaction.core.compensation.impl;

import com.happylifeplat.transaction.common.enums.CompensationActionEnum;
import com.happylifeplat.transaction.common.enums.TransactionStatusEnum;
import com.happylifeplat.transaction.common.holder.LogUtil;
import com.happylifeplat.transaction.common.netty.bean.TxTransactionGroup;
import com.happylifeplat.transaction.common.netty.bean.TxTransactionItem;
import com.happylifeplat.transaction.core.bean.TransactionInvocation;
import com.happylifeplat.transaction.core.bean.TransactionRecover;
import com.happylifeplat.transaction.core.compensation.TxCompensationService;
import com.happylifeplat.transaction.core.compensation.command.TxCompensationAction;
import com.happylifeplat.transaction.core.concurrent.threadlocal.CompensationLocal;
import com.happylifeplat.transaction.core.concurrent.threadpool.TransactionThreadPool;
import com.happylifeplat.transaction.core.concurrent.threadpool.TxTransactionThreadFactory;
import com.happylifeplat.transaction.core.constant.Constant;
import com.happylifeplat.transaction.core.helper.SpringBeanUtils;
import com.happylifeplat.transaction.core.config.TxConfig;
import com.happylifeplat.transaction.core.service.ModelNameService;
import com.happylifeplat.transaction.core.service.TxManagerMessageService;
import com.happylifeplat.transaction.core.spi.TransactionRecoverRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * 补偿实现类
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/19 14:14
 * @since JDK 1.8
 */
@Service
public class TxCompensationServiceImpl implements TxCompensationService {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TxCompensationServiceImpl.class);


    private TransactionRecoverRepository transactionRecoverRepository;

    private final ModelNameService modelNameService;

    private TxConfig txConfig;


    private final TxManagerMessageService txManagerMessageService;

    private ScheduledExecutorService scheduledExecutorService;


    private static BlockingQueue<TxCompensationAction> QUEUE;

    @Autowired
    public TxCompensationServiceImpl(ModelNameService modelNameService, TxManagerMessageService txManagerMessageService) {
        this.modelNameService = modelNameService;
        this.txManagerMessageService = txManagerMessageService;
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(
                TxTransactionThreadFactory.create("CompensationService", true));
    }

    @Override
    public void compensate() {
        scheduledExecutorService
                .scheduleAtFixedRate(() -> {
                    LogUtil.debug(LOGGER, "compensate execute delayTime:{}", () -> txConfig.getCompensationRecoverTime());
                    try {
                        final List<TransactionRecover> transactionRecovers = transactionRecoverRepository.listAll();
                        if (CollectionUtils.isNotEmpty(transactionRecovers)) {
                            transactionRecovers.stream().map(transactionRecover -> {
                                transactionRecoverRepository.update(transactionRecover);
                                final TxTransactionGroup byTxGroupId = txManagerMessageService
                                        .findByTxGroupId(transactionRecover.getGroupId());
                                if (Objects.nonNull(byTxGroupId) && CollectionUtils.isNotEmpty(byTxGroupId.getItemList())) {
                                    final Optional<TxTransactionItem> any = byTxGroupId.getItemList().stream()
                                            .filter(item -> Objects.equals(item.getTaskKey(), transactionRecover.getGroupId()))
                                            .findAny();
                                    if (any.isPresent()) {
                                        final int status = any.get().getStatus();
                                        if (TransactionStatusEnum.COMMIT.getCode() == status) {
                                            final Optional<TxTransactionItem> txTransactionItem = byTxGroupId.getItemList().stream()
                                                    .filter(item -> Objects.equals(item.getTaskKey(), transactionRecover.getTaskId()))
                                                    .findAny();
                                            if (txTransactionItem.isPresent()) {
                                                final TxTransactionItem item = txTransactionItem.get();
                                                if (item.getStatus() != TransactionStatusEnum.COMMIT.getCode()) {
                                                    return buildCompensate(transactionRecover);
                                                }
                                            }
                                        } else {
                                            //删除本地补偿信息
                                            LogUtil.info(LOGGER, "事务组id：{} ,未提交，不需要进行补偿!",
                                                    transactionRecover::getGroupId);
                                            return buildDel(transactionRecover);
                                        }

                                    }
                                } else {
                                    //删除本地补偿信息
                                    LogUtil.info(LOGGER, "事务组id：{} ,未提交，不需要进行补偿!",
                                            transactionRecover::getGroupId);
                                    return buildDel(transactionRecover);
                                }
                                return null;
                            }).filter(Objects::nonNull).forEach(this::submit);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 30, txConfig.getCompensationRecoverTime(), TimeUnit.SECONDS);

    }




    /**
     * 启动本地补偿事务，根据配置是否进行补偿
     */
    @Override
    public void start(TxConfig txConfig) throws Exception {
        this.txConfig = txConfig;
        if (txConfig.getCompensation()) {
            final String modelName = modelNameService.findModelName();
            transactionRecoverRepository = SpringBeanUtils.getInstance().getBean(TransactionRecoverRepository.class);
            transactionRecoverRepository.init(modelName, txConfig);
            initCompensatePool();//初始化补偿操作的线程池
            compensate();//执行定时补偿
        }
    }

    public void initCompensatePool() {
        synchronized (LOGGER) {
            QUEUE = new LinkedBlockingQueue<>(txConfig.getCompensationQueueMax());
            final int compensationThreadMax = txConfig.getCompensationThreadMax();
            final TransactionThreadPool threadPool = SpringBeanUtils.getInstance().getBean(TransactionThreadPool.class);
            final ExecutorService executorService = threadPool.newCustomFixedThreadPool(compensationThreadMax);
            LogUtil.info(LOGGER, "启动补偿操作线程数量为:{}", () -> compensationThreadMax);
            for (int i = 0; i < compensationThreadMax; i++) {
                executorService.execute(new Worker());
            }

        }
    }

    /**
     * 保存补偿事务信息
     *
     * @param transactionRecover 实体对象
     * @return 主键id
     */
    @Override
    public String save(TransactionRecover transactionRecover) {
        final int rows = transactionRecoverRepository.create(transactionRecover);
        if (rows > 0) {
            return transactionRecover.getId();
        }
        return null;

    }

    /**
     * 删除补偿事务信息
     *
     * @param id 主键id
     * @return true成功 false 失败
     */
    @Override
    public boolean remove(String id) {
        final int rows = transactionRecoverRepository.remove(id);
        return rows > 0;
    }

    /**
     * 更新
     *
     * @param transactionRecover 实体对象
     */
    @Override
    public void update(TransactionRecover transactionRecover) {
        transactionRecoverRepository.update(transactionRecover);
    }

    /**
     * 提交补偿操作
     *
     * @param txCompensationAction 补偿命令
     */
    @Override
    public Boolean submit(TxCompensationAction txCompensationAction) {
        try {
            if (txConfig.getCompensation()) {
                QUEUE.put(txCompensationAction);
            }
        } catch (InterruptedException e) {
            LogUtil.error(LOGGER, "补偿命令提交队列失败：{}", e::getMessage);
            return false;

        }
        return true;
    }

    /**
     * 线程执行器
     */
    class Worker implements Runnable {

        @Override
        public void run() {
            execute();
        }

        /**
         * 事务执行..
         */
        private void execute() {
            while (true) {
                try {
                    TxCompensationAction transaction = QUEUE.take();//得到需要回滚的事务对象
                    if (transaction != null) {
                        final int code = transaction.getCompensationActionEnum().getCode();
                        if (CompensationActionEnum.SAVE.getCode() == code) {
                            save(transaction.getTransactionRecover());
                        } else if (CompensationActionEnum.DELETE.getCode() == code) {
                            remove(transaction.getTransactionRecover().getId());
                        } else if (CompensationActionEnum.UPDATE.getCode() == code) {
                            update(transaction.getTransactionRecover());
                        } else if (CompensationActionEnum.COMPENSATE.getCode() == code) {
                            compensatoryTransfer(transaction.getTransactionRecover());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.error(LOGGER, "执行补偿命令失败：{}", e::getMessage);
                }
            }

        }
    }

    /**
     * 执行补偿
     *
     * @param transactionRecover 补偿信息
     */
    @SuppressWarnings("unchecked")
    private void compensatoryTransfer(TransactionRecover transactionRecover) {
        if (Objects.nonNull(transactionRecover)) {
            final TransactionInvocation transactionInvocation = transactionRecover.getTransactionInvocation();
            if (Objects.nonNull(transactionInvocation)) {
                final Class clazz = transactionInvocation.getTargetClazz();
                final String method = transactionInvocation.getMethod();
                final Object[] argumentValues = transactionInvocation.getArgumentValues();
                final Class[] argumentTypes = transactionInvocation.getArgumentTypes();
                final Object bean = SpringBeanUtils.getInstance().getBean(clazz);
                try {
                    CompensationLocal.getInstance().setCompensationId(Constant.COMPENSATE_ID);
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

    private TxCompensationAction buildCompensate(TransactionRecover transactionRecover) {
        TxCompensationAction compensationAction = new TxCompensationAction();
        compensationAction.setCompensationActionEnum(CompensationActionEnum.COMPENSATE);
        compensationAction.setTransactionRecover(transactionRecover);
        return compensationAction;
    }

    private TxCompensationAction buildDel(TransactionRecover transactionRecover) {
        TxCompensationAction compensationAction = new TxCompensationAction();
        compensationAction.setCompensationActionEnum(CompensationActionEnum.DELETE);
        compensationAction.setTransactionRecover(transactionRecover);
        return compensationAction;
    }

}
