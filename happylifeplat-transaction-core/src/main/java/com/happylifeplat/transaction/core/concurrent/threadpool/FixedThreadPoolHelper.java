package com.happylifeplat.transaction.core.concurrent.threadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * 线程池帮助类
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/4/5 9:05
 * @since JDK 1.8
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
        return  Executors.newFixedThreadPool(DEFAULT_THREAD_MAX, threadFactory);
    }

    public int getDefaultThreadMax(){
        return  DEFAULT_THREAD_MAX;
    }
}
