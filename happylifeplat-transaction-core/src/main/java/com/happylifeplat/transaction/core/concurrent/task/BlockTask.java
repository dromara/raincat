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
package com.happylifeplat.transaction.core.concurrent.task;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xiaoyu
 */
public class BlockTask {

    private Lock lock;
    private Condition condition;
    private CountDownLatch countDownLatch;

    private AsyncCall asyncCall;

    /**
     * 是否被唤醒
     */
    private volatile static boolean Notify = false;

    /**
     * 是否被唤醒
     */
    private volatile static boolean remove = false;

    /**
     * 唯一标示key
     */
    private String key;

    /**
     * 数据状态用于业务处理
     */
    private int state = 0;


    public BlockTask() {
        lock = new ReentrantLock();
        condition = lock.newCondition();
        //countDownLatch = new CountDownLatch(1);

    }

    public void countDownAwait() {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            countDownLatch.countDown();
        }
    }

    public void countDown() {
        countDownLatch.countDown();
    }


    public void signal() {
        try {
            lock.lock();
            Notify = true;
            condition.signal();
        } finally {
            lock.unlock();
        }

    }


    public void await() {
        try {
            lock.lock();
            condition.await();
        } catch (Exception e) {
        } finally {
            lock.unlock();
        }
    }


    public void setAsyncCall(AsyncCall asyncCall) {
        this.asyncCall = asyncCall;
    }

    public AsyncCall getAsyncCall() {
        return asyncCall;
    }

    public boolean isNotify() {
        return Notify;
    }

    public static void setNotify(boolean notify) {
        Notify = notify;
    }

    public static boolean isRemove() {
        return remove;
    }

    public static void setRemove(boolean remove) {
        BlockTask.remove = remove;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }


}
