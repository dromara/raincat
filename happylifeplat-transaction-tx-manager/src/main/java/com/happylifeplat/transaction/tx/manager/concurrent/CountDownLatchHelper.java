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
package com.happylifeplat.transaction.tx.manager.concurrent;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaoyu
 */
public class CountDownLatchHelper<T> {

    private static volatile boolean isExecute = false;
    private final List<T> data;
    private CountDownLatch end;
    private ExecutorService threadPool = null;
    private List<IExecute<T>> executes = null;

   private  static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("demo-pool-%d").build();

    public CountDownLatchHelper() {
        executes = Lists.newCopyOnWriteArrayList();
        data = Lists.newCopyOnWriteArrayList();
        threadPool= new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(),
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

    }

    public CountDownLatchHelper<T> addExecute(IExecute<T> execute) {
        executes.add(execute);
        return this;
    }

    @SuppressWarnings("unchecked")
    public CountDownLatchHelper<T> execute() {
        int count = executes.size();
        if (count > 0) {
            end = new CountDownLatch(count);
            for (IExecute<T> countDown : executes) {
                CountDownThread countDownThread = new CountDownThread(threadPool, data, countDown, end);
                countDownThread.execute();
            }
            try {
                end.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        threadPool.shutdown();
        isExecute = true;
        return this;
    }

    public List<T> getData() {
        if (!isExecute) {
            throw new RuntimeException("no execute !");
        }
        return data;
    }

}
