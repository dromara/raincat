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

}