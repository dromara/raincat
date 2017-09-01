package com.happylifeplat.transaction.tx.manager.concurrent;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;



public class CountDownThread<T> implements Runnable {

    private ExecutorService threadPool;

    public CountDownThread(ExecutorService threadPool, List<T> list, IExecute<T> execute, CountDownLatch currentThread) {
        this.threadPool = threadPool;
        this.list = list;
        this.execute = execute;
        this.currentThread = currentThread;
    }

    private CountDownLatch currentThread;

    private IExecute<T> execute;

    private List<T> list;

    @Override
    public void run() {
        list.add(execute.execute());
        currentThread.countDown();
    }

    public void execute() {
        threadPool.execute(this);
    }


}
