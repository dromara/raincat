/*
 *
 *  * Licensed to the Apache Software Foundation (ASF) under one or more
 *  * contributor license agreements.  See the NOTICE file distributed with
 *  * this work for additional information regarding copyright ownership.
 *  * The ASF licenses this file to You under the Apache License, Version 2.0
 *  * (the "License"); you may not use this file except in compliance with
 *  * the License.  You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.dromara.raincat.core.disruptor.publisher;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.dromara.raincat.common.bean.TransactionRecover;
import org.dromara.raincat.common.config.TxConfig;
import org.dromara.raincat.common.enums.CompensationActionEnum;
import org.dromara.raincat.core.compensation.TxCompensationService;
import org.dromara.raincat.core.concurrent.threadpool.TxTransactionThreadFactory;
import org.dromara.raincat.core.disruptor.event.TxTransactionEvent;
import org.dromara.raincat.core.disruptor.factory.TxTransactionEventFactory;
import org.dromara.raincat.core.disruptor.handler.TxTransactionEventHandler;
import org.dromara.raincat.core.disruptor.translator.TxTransactionEventTranslator;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * event publisher.
 *
 * @author xiaoyu(Myth)
 */
@Component
public class TxTransactionEventPublisher implements DisposableBean, ApplicationListener<ContextRefreshedEvent> {

    private static final AtomicLong INDEX = new AtomicLong(1);

    private Disruptor<TxTransactionEvent> disruptor;

    private final TxCompensationService txCompensationService;

    private final TxConfig txConfig;

    @Autowired
    public TxTransactionEventPublisher(final TxCompensationService txCompensationService,
                                       final TxConfig txConfig) {
        this.txCompensationService = txCompensationService;
        this.txConfig = txConfig;
    }

    /**
     * disruptor start.
     *
     * @param bufferSize this is disruptor buffer size.
     * @param threads this is disruptor consumer thread size.
     */
    private void start(final int bufferSize, final int threads) {
        disruptor = new Disruptor<>(new TxTransactionEventFactory(), bufferSize, r -> {
            return new Thread(null, r, "disruptor-thread-" + INDEX.getAndIncrement());
        }, ProducerType.MULTI, new BlockingWaitStrategy());

        final Executor executor = new ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                TxTransactionThreadFactory.create("raincat-log-disruptor", false),
                new ThreadPoolExecutor.AbortPolicy());

        TxTransactionEventHandler[] consumers = new TxTransactionEventHandler[threads];
        for (int i = 0; i < threads; i++) {
            consumers[i] = new TxTransactionEventHandler(executor, txCompensationService);
        }
        disruptor.handleEventsWithWorkerPool(consumers);
        disruptor.setDefaultExceptionHandler(new IgnoreExceptionHandler());
        disruptor.start();
    }

    /**
     * publish disruptor event.
     *
     * @param transactionRecover {@linkplain TransactionRecover }
     * @param type               {@linkplain CompensationActionEnum}
     */
    public void publishEvent(final TransactionRecover transactionRecover, final int type) {
        if (txConfig.getCompensation()) {
            final RingBuffer<TxTransactionEvent> ringBuffer = disruptor.getRingBuffer();
            ringBuffer.publishEvent(new TxTransactionEventTranslator(type), transactionRecover);
        }
    }

    @Override
    public void destroy() {
        disruptor.shutdown();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        start(txConfig.getBufferSize(), txConfig.getConsumerThreads());
    }
}
