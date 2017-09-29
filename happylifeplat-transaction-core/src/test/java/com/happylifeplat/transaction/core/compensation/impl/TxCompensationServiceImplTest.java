package com.happylifeplat.transaction.core.compensation.impl;

import com.happylifeplat.transaction.core.compensation.command.TxCompensationAction;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TxCompensationServiceImplTest {


    @Autowired
    TxCompensationServiceImpl txCompensationService;


    @Test
    public void start() throws Exception {
        txCompensationService.initCompensatePool();
        txCompensationService.submit(new TxCompensationAction());
        try {
            Thread.currentThread().sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void save() throws Exception {
    }

    @Test
    public void remove() throws Exception {
    }

    @Test
    public void update() throws Exception {
    }

    @Test
    public void submit() throws Exception {
    }

}