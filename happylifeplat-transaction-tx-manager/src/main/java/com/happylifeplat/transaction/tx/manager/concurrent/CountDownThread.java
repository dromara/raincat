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

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;


/**
 * @author xiaoyu
 */
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
