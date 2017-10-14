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
package com.happylifeplat.transaction.common.holder.httpclient;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.happylifeplat.transaction.common.holder.IdWorkerUtils;
import com.happylifeplat.transaction.common.netty.bean.TxTransactionItem;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class OkHttpToolsTest {

    private static  final Gson GSON = new Gson();

    @Test
    public void post() throws Exception {

        List<TxTransactionItem> itemList = Lists.newArrayList();
        TxTransactionItem txTransactionItem = new TxTransactionItem();
        txTransactionItem.setTaskKey(IdWorkerUtils.getInstance().createTaskKey());
        itemList.add(txTransactionItem);
        OkHttpTools.getInstance().post("http://192.168.1.66:8761/tx/manager/httpExecute",GSON.toJson(itemList));

    }


    @Test
    public void test01(){
        MyThread myThread = null;
        for (int i=0;i<50;i++){
            MyThread  m = new MyThread("name"+i,myThread);
            m.setDaemon(true);
            m.start();
            myThread = m;
        }
        System.out.println("完成");
    }

    @Test
    public void test02(){
        AtomicInteger atomicInteger  = new AtomicInteger();

        for (int i=0;i<50;i++){
            SortThread  m = new SortThread("name"+i,atomicInteger,i);
            m.setDaemon(true);
            m.start();

        }
        System.out.println("完成");
    }

    class SortThread extends Thread{

        AtomicInteger atomicInteger ;
        Integer order ;
        public  SortThread(String name,AtomicInteger atomicInteger,int order){
            super(name);
            this.atomicInteger = atomicInteger;
            this.order = order;
        }

        @Override
        public void run() {
            while (true) {
                System.out.println(Thread.currentThread().getName() + " " +atomicInteger.get() + "  "+order);
                if (atomicInteger.get()    == order) {
                    System.out.println(Thread.currentThread().getName() + " 执行跳槽 " + order);
                    atomicInteger.incrementAndGet();
                    break;
                }

            }
        }
    }

    class MyThread extends Thread{
        Thread thread ;
        public MyThread(String name,Thread thread){
            super(name);
            this.thread  = thread;
        }
        @Override
        public void run() {
            if(thread != null)
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            System.out.println(Thread.currentThread().getName()+"  执行。。。。。。");
        }
    }

}