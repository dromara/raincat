package com.happylifeplat.transaction.core.compensation.impl;

import com.happylifeplat.transaction.core.compensation.TxCompensationService;
import com.happylifeplat.transaction.core.compensation.command.TxCompensationAction;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/25 12:26
 * @since JDK 1.8
 */
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