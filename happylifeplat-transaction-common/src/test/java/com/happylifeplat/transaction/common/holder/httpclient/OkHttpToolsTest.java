package com.happylifeplat.transaction.common.holder.httpclient;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.happylifeplat.transaction.common.holder.IdWorkerUtils;
import com.happylifeplat.transaction.common.netty.bean.TxTransactionItem;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/8/8 17:06
 * @since JDK 1.8
 */

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

}