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

package com.raincat.core.concurrent.task;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * BlockTask.
 * @author xiaoyu
 */
public class BlockTask {

    private Lock lock;

    private Condition condition;

    private AsyncCall asyncCall;

    private volatile boolean notify;

    private volatile boolean remove;

    private String key;

    public BlockTask() {
        lock = new ReentrantLock();
        condition = lock.newCondition();
        notify = false;
        remove = false;
    }

    public void signal() {
        try {
            lock.lock();
            notify = true;
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    public void await() {
        try {
            lock.lock();
            if(!isNotify()) {
                condition.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void setAsyncCall(final AsyncCall asyncCall) {
        this.asyncCall = asyncCall;
    }

    public AsyncCall getAsyncCall() {
        return asyncCall;
    }

    public boolean isNotify() {
        return notify;
    }

    public boolean isRemove() {
        return remove;
    }

    public void setRemove(final boolean remove) {
        this.remove = remove;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

}
