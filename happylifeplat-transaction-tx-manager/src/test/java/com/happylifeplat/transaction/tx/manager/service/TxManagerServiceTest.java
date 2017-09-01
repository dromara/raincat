package com.happylifeplat.transaction.tx.manager.service;

import com.happylifeplat.transaction.common.holder.IdWorkerUtils;
import com.happylifeplat.transaction.common.netty.bean.TxTransactionGroup;
import com.happylifeplat.transaction.common.netty.bean.TxTransactionItem;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;


/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/8/8 14:13
 * @since JDK 1.8
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TxManagerServiceTest {


    @Autowired
    private TxManagerService txManagerService;


    @Test
    public void saveTxTransactionGroup() throws Exception {

        TxTransactionGroup txTransactionGroup  = new TxTransactionGroup();
        txTransactionGroup.setId(IdWorkerUtils.getInstance().createGroupId());


        TxTransactionItem  item = new TxTransactionItem();
        item.setStatus(5);
        item.setTaskKey(IdWorkerUtils.getInstance().createTaskKey());

        txTransactionGroup.setItemList(Collections.singletonList(item));

        txManagerService.saveTxTransactionGroup(txTransactionGroup);
    }

    @Test
    public void addTxTransaction() throws Exception {
    }

    @Test
    public void listByTxGroupId() throws Exception {
        final List<TxTransactionItem> txTransactionItems = txManagerService.listByTxGroupId("872308019");
        Assert.assertNotNull(txTransactionItems);
    }

    @Test
    public void removeRedisByTxGroupId() throws Exception {
    }

    @Test
    public void updateTxTransactionItemStatus() throws Exception {
    }

    @Test
    public void findTxTransactionGroupStatus() throws Exception {
    }

    @Test
    public void removeCommitTxGroup() throws Exception {
    }

}