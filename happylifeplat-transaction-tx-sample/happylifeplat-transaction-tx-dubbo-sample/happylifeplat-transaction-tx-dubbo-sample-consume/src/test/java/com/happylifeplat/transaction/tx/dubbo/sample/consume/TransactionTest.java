package com.happylifeplat.transaction.tx.dubbo.sample.consume;

import com.happylifeplat.transaction.tx.dubbo.sample.consume.service.Test1Service;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionTest {

    @Autowired
    private Test1Service test1Service;

    private Lock lock = new ReentrantLock();


    @Test
    public void testFail() {

        String name = test1Service.testFail();


    }

    @Test
    public void test() {


        final ExecutorService executorService = Executors.newFixedThreadPool(1);


        for (int i = 0; i < 1; i++) {
            executorService.execute(new work());
        }

        try {
            Thread.sleep(1000000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    class work implements Runnable {

        @Override
        public void run() {
            test1Service.save();
        }
    }

}
