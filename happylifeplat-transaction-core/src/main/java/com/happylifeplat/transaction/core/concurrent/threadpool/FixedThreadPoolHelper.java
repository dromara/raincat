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
package com.happylifeplat.transaction.core.concurrent.threadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaoyu
 */
public class FixedThreadPoolHelper {

    private static final FixedThreadPoolHelper INSTANCE = new FixedThreadPoolHelper();

    /**
     * 线程数量大小
     */
    private static final int DEFAULT_THREAD_MAX = Runtime.getRuntime().availableProcessors();


    private FixedThreadPoolHelper() {
    }

    public static FixedThreadPoolHelper getInstance() {
        return INSTANCE;
    }


    public ExecutorService getExecutorService() {
        final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("search-thread-%d")
                .setDaemon(true).build();
        return new ThreadPoolExecutor(DEFAULT_THREAD_MAX, DEFAULT_THREAD_MAX,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024), threadFactory, new ThreadPoolExecutor.AbortPolicy());

    }

    public int getDefaultThreadMax() {
        return DEFAULT_THREAD_MAX;
    }
}
