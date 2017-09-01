package com.happylifeplat.transaction.tx.manager.concurrent;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountDownLatchHelper<T> {

    private static volatile boolean isExecute = false;
    private final List<T> data;
    private CountDownLatch end;
    private ExecutorService threadPool = null;
    private List<IExecute<T>> executes = null;

    public CountDownLatchHelper() {
        threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        executes = Lists.newCopyOnWriteArrayList();
        data = Lists.newCopyOnWriteArrayList();
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
        if (!isExecute)
            throw new RuntimeException("no execute !");
        return data;
    }

}
