package com.raincat.common.holder.httpclient;

/**
 * <p>Description: .</p>
 *
 * @author xiaoyu(Myth)
 * @version 1.0
 * @date 2018/2/5 10:53
 * @since JDK 1.8
 */
public class ThreadLocalMapTest {

    public static void main(String[] args) {

        ThreadLocal A = new ThreadLocal<String>();

        ThreadLocal B = new ThreadLocal<String>();

        for(int i = 0 ;i<10 ;i++){
            A.set("3");
            B.set("4");
        }

    }

}
