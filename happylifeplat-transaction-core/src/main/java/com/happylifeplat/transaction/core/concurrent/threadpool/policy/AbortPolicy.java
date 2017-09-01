
package com.happylifeplat.transaction.core.concurrent.threadpool.policy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;



/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  线程池AbortPolicy 策略
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/17 20:56
 * @since JDK 1.8
 */
public class AbortPolicy extends ThreadPoolExecutor.AbortPolicy {
    private static final Logger LOG = LoggerFactory.getLogger(AbortPolicy.class);

    private String threadName;

    public AbortPolicy() {
        this(null);
    }

    public AbortPolicy(String threadName) {
        this.threadName = threadName;
    }

    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        if (threadName != null) {
            LOG.error("txTransaction Thread pool [{}] is exhausted, executor={}", threadName, executor.toString());
        }
        String msg = String.format("txTransaction["
                        + " Thread Name: %s, Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: %d),"
                        + " Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s)]",
                threadName, executor.getPoolSize(), executor.getActiveCount(), executor.getCorePoolSize(), executor.getMaximumPoolSize(), executor.getLargestPoolSize(),
                executor.getTaskCount(), executor.getCompletedTaskCount(), executor.isShutdown(), executor.isTerminated(), executor.isTerminating());
        System.out.println(msg);
        super.rejectedExecution(runnable, executor);
    }
}

