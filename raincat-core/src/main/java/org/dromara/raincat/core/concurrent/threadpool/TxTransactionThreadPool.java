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

package org.dromara.raincat.core.concurrent.threadpool;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * TransactionThreadPool.
 * @author xiaoyu
 */
@Component
public class TxTransactionThreadPool {

    private static final int MAX_THREAD = Runtime.getRuntime().availableProcessors() << 1;

    private static final String THREAD_FACTORY_NAME = "txTransaction";

    private ScheduledExecutorService scheduledExecutorService;

    private ExecutorService fixExecutorService;

    @PostConstruct
    public void init() {
        scheduledExecutorService = new ScheduledThreadPoolExecutor(MAX_THREAD,
                TxTransactionThreadFactory.create(THREAD_FACTORY_NAME, false));
        fixExecutorService = new ThreadPoolExecutor(MAX_THREAD, MAX_THREAD, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                TxTransactionThreadFactory.create(THREAD_FACTORY_NAME, false), new ThreadPoolExecutor.AbortPolicy());
    }

    public ExecutorService newFixedThreadPool() {
        return fixExecutorService;
    }

    public ScheduledFuture multiScheduled(final Supplier<Object> supplier,
                                          final int waitTime) {
        return scheduledExecutorService.schedule((Runnable) supplier::get, waitTime, TimeUnit.SECONDS);

    }

}

