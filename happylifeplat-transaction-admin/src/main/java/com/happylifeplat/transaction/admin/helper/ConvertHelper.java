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

package com.happylifeplat.transaction.admin.helper;

import com.happylifeplat.transaction.admin.vo.TransactionRecoverVO;
import com.happylifeplat.transaction.admin.vo.TxTransactionItemVO;
import com.happylifeplat.transaction.common.bean.TransactionRecover;
import com.happylifeplat.transaction.common.bean.adapter.TransactionRecoverAdapter;
import com.happylifeplat.transaction.common.enums.TransactionRoleEnum;
import com.happylifeplat.transaction.common.enums.TransactionStatusEnum;
import com.happylifeplat.transaction.common.holder.DateUtils;
import com.happylifeplat.transaction.common.netty.bean.TxTransactionItem;

import java.text.SimpleDateFormat;

/**
 * <p>Description: .</p>
 *
 * @author xiaoyu(Myth)
 * @version 1.0
 * @date 2017/10/23 11:53
 * @since JDK 1.8
 */
public class ConvertHelper {

    public static TransactionRecoverVO buildVO(TransactionRecoverAdapter adapter) {
        TransactionRecoverVO vo = new TransactionRecoverVO();
        vo.setId(adapter.getTransId());
        vo.setCreateTime(DateUtils.parseDate(adapter.getCreateTime()));
        vo.setGroupId(adapter.getGroupId());
        vo.setRetriedCount(adapter.getRetriedCount());
        vo.setLastTime(DateUtils.parseDate(adapter.getLastTime()));
        vo.setTaskId(adapter.getTaskId());
        vo.setVersion(adapter.getVersion());
        vo.setTargetClass(adapter.getTargetClass());
        vo.setTargetMethod(adapter.getTargetMethod());
        return vo;

    }

    public static TxTransactionItemVO buildTxItemVO(TxTransactionItem item) {
        TxTransactionItemVO vo = new TxTransactionItemVO();
        vo.setCreateDate(item.getCreateDate());
        vo.setModelName(item.getModelName());
        vo.setRole(TransactionRoleEnum.acquireDescByCode(item.getRole()));
        vo.setStatus(TransactionStatusEnum.acquireDescByCode(item.getStatus()));
        vo.setTargetClass(item.getTargetClass());
        vo.setTransId(item.getTransId());
        vo.setTargetMethod(item.getTargetMethod());
        vo.setModelName(item.getModelName());
        vo.setTmDomain(item.getTmDomain());
        vo.setTaskKey(item.getTaskKey());
        vo.setTxGroupId(item.getTxGroupId());
        vo.setWaitMaxTime(item.getWaitMaxTime());
        vo.setConsumeTime(item.getConsumeTime());
        vo.setMessage(item.getMessage());
        return vo;
    }
}
