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

package com.happylifeplat.transaction.common.holder;

import com.happylifeplat.transaction.common.bean.TransactionInvocation;
import com.happylifeplat.transaction.common.bean.TransactionRecover;
import com.happylifeplat.transaction.common.bean.adapter.TransactionRecoverAdapter;
import com.happylifeplat.transaction.common.exception.TransactionException;
import com.happylifeplat.transaction.common.serializer.ObjectSerializer;

/**
 * <p>Description: .</p>
 *
 * @author xiaoyu(Myth)
 * @version 1.0
 * @date 2017/10/30 11:04
 * @since JDK 1.8
 */
public class TransactionRecoverUtils {


    public static byte[] convert(TransactionRecover transactionRecover,
                                 ObjectSerializer objectSerializer) throws TransactionException {
        TransactionRecoverAdapter adapter = new TransactionRecoverAdapter();
        final TransactionInvocation transactionInvocation = transactionRecover.getTransactionInvocation();
        adapter.setGroupId(transactionRecover.getGroupId());
        adapter.setLastTime(transactionRecover.getLastTime());
        adapter.setRetriedCount(transactionRecover.getRetriedCount());
        adapter.setStatus(transactionRecover.getStatus());
        adapter.setTaskId(transactionRecover.getTaskId());
        adapter.setTransId(transactionRecover.getId());
        adapter.setTargetClass(transactionInvocation.getTargetClazz().getName());
        adapter.setTargetMethod(transactionInvocation.getMethod());
        adapter.setCreateTime(transactionRecover.getCreateTime());
        adapter.setContents(objectSerializer.serialize(transactionInvocation));
        adapter.setVersion(transactionRecover.getVersion());
        return objectSerializer.serialize(adapter);
    }



    public static TransactionRecover transformVO(byte[] contents,
                                                   ObjectSerializer objectSerializer) throws TransactionException {
        TransactionRecover transactionRecover = new TransactionRecover();

        final TransactionRecoverAdapter adapter =
                objectSerializer.deSerialize(contents, TransactionRecoverAdapter.class);


        transactionRecover.setLastTime(adapter.getLastTime());
        transactionRecover.setRetriedCount(adapter.getRetriedCount());
        transactionRecover.setCreateTime(adapter.getCreateTime());
        transactionRecover.setGroupId(adapter.getGroupId());
        transactionRecover.setId(adapter.getTransId());
        transactionRecover.setTaskId(adapter.getTaskId());
        transactionRecover.setStatus(adapter.getStatus());

        transactionRecover.setVersion(adapter.getVersion());
        return transactionRecover;


    }



    public static TransactionRecover transformBean(byte[] contents,
                                                   ObjectSerializer objectSerializer) throws TransactionException {
        TransactionRecover transactionRecover = new TransactionRecover();

        final TransactionRecoverAdapter adapter =
                objectSerializer.deSerialize(contents, TransactionRecoverAdapter.class);

        TransactionInvocation transactionInvocation =
                objectSerializer.deSerialize(adapter.getContents(), TransactionInvocation.class);

        transactionRecover.setLastTime(adapter.getLastTime());
        transactionRecover.setRetriedCount(adapter.getRetriedCount());
        transactionRecover.setCreateTime(adapter.getCreateTime());
        transactionRecover.setGroupId(adapter.getGroupId());
        transactionRecover.setId(adapter.getTransId());
        transactionRecover.setTaskId(adapter.getTaskId());
        transactionRecover.setStatus(adapter.getStatus());
        transactionRecover.setTransactionInvocation(transactionInvocation);
        transactionRecover.setVersion(adapter.getVersion());
        return transactionRecover;


    }
}
