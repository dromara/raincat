
package com.happylifeplat.transaction.core.concurrent.threadpool;


import com.happylifeplat.transaction.common.enums.BlockingQueueTypeEnum;
import com.happylifeplat.transaction.common.enums.RejectedPolicyTypeEnum;
import com.happylifeplat.transaction.common.holder.LogUtil;
import com.happylifeplat.transaction.core.concurrent.threadpool.policy.AbortPolicy;
import com.happylifeplat.transaction.core.concurrent.threadpool.policy.BlockingPolicy;
import com.happylifeplat.transaction.core.concurrent.threadpool.policy.CallerRunsPolicy;
import com.happylifeplat.transaction.core.concurrent.threadpool.policy.DiscardedPolicy;
import com.happylifeplat.transaction.core.concurrent.threadpool.policy.RejectedPolicy;
import com.happylifeplat.transaction.core.config.TxConfig;
import com.happylifeplat.transaction.core.helper.SpringBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * 获取线程池帮助类
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/17 20:56
 * @since JDK 1.8
 */
@Component
public class TransactionThreadPool {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionThreadPool.class);

    private static final String ThreadFactoryName = "txTransaction";
    private static final int Max_Array_Queue = 1000;

    private TxConfig txConfig;

    private ScheduledExecutorService scheduledExecutorService;

    private ExecutorService fixExecutorService;

    private static final ScheduledExecutorService singleThreadScheduledExecutor =
            Executors.newSingleThreadScheduledExecutor(TxTransactionThreadFactory.create(ThreadFactoryName, false));


    @PostConstruct
    public void init() {
        scheduledExecutorService = Executors.newScheduledThreadPool(txConfig.getTransactionThreadMax(),
                TxTransactionThreadFactory.create(ThreadFactoryName, true));

        fixExecutorService = new ThreadPoolExecutor(txConfig.getTransactionThreadMax(), txConfig.getTransactionThreadMax(), 0, TimeUnit.MILLISECONDS,
                createBlockingQueue(),
                TxTransactionThreadFactory.create(ThreadFactoryName, false), createPolicy());

    }


    @Autowired
    public TransactionThreadPool(TxConfig txConfig) {
        this.txConfig = txConfig;
    }


    private RejectedExecutionHandler createPolicy() {
        RejectedPolicyTypeEnum rejectedPolicyType = RejectedPolicyTypeEnum.fromString(txConfig.getRejectPolicy());

        switch (rejectedPolicyType) {
            case BLOCKING_POLICY:
                return new BlockingPolicy();
            case CALLER_RUNS_POLICY:
                return new CallerRunsPolicy();
            case ABORT_POLICY:
                return new AbortPolicy();
            case REJECTED_POLICY:
                return new RejectedPolicy();
            case DISCARDED_POLICY:
                return new DiscardedPolicy();
            default:
                return new AbortPolicy();
        }
    }

    private BlockingQueue<Runnable> createBlockingQueue() {
        BlockingQueueTypeEnum queueType = BlockingQueueTypeEnum.fromString(txConfig.getBlockingQueueType());

        switch (queueType) {
            case LINKED_BLOCKING_QUEUE:
                return new LinkedBlockingQueue<>();
            case ARRAY_BLOCKING_QUEUE:
                return new ArrayBlockingQueue<>(Max_Array_Queue);
            case SYNCHRONOUS_QUEUE:
                return new SynchronousQueue<>();
            default:
                return new LinkedBlockingQueue<>();
        }

    }

    public ExecutorService newCustomFixedThreadPool(int threads) {
        return new ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS,
                createBlockingQueue(),
                TxTransactionThreadFactory.create(ThreadFactoryName, false), createPolicy());
    }

    public ExecutorService newFixedThreadPool() {
        return fixExecutorService;
    }

    public ExecutorService newSingleThreadExecutor() {
        return new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
                createBlockingQueue(),
                TxTransactionThreadFactory.create(ThreadFactoryName, false), createPolicy());
    }

    public ScheduledExecutorService newSingleThreadScheduledExecutor() {
        return singleThreadScheduledExecutor;
    }

    public ScheduledExecutorService newScheduledThreadPool() {
        return scheduledExecutorService;
    }


    public ScheduledFuture multiThreadscheduled(Supplier<Object> supplier) {
        return scheduledExecutorService
                .schedule(() -> {
                            final Boolean o = (Boolean) supplier.get();
                            if (o) {
                                LogUtil.info(LOGGER, "多线程执行任务调度成功,调度时间为:{}", txConfig::getDelayTime);
                            } else {
                                LogUtil.info(LOGGER, "多线程执行任务调度未执行任务,调度时间为:{}", txConfig::getDelayTime);
                            }
                        },
                        txConfig.getDelayTime(), TimeUnit.SECONDS);
    }

    public ScheduledFuture singleThreadScheduled(Supplier<Object> supplier) {
        return singleThreadScheduledExecutor
                .schedule(() -> {
                            final Boolean o = (Boolean) supplier.get();
                            if (o) {
                                LogUtil.info(LOGGER, "单线程执行任务调度成功,调度时间为:{}", txConfig::getDelayTime);
                            } else {
                                LogUtil.info(LOGGER, "单线程执行任务调度未执行任务,调度时间为:{}", txConfig::getDelayTime);
                            }
                        },
                        txConfig.getDelayTime(), TimeUnit.SECONDS);
    }

}

