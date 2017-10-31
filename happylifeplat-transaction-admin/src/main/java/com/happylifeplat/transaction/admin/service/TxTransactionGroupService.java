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

package com.happylifeplat.transaction.admin.service;

import com.happylifeplat.transaction.admin.page.CommonPager;
import com.happylifeplat.transaction.admin.query.TxTransactionQuery;
import com.happylifeplat.transaction.admin.vo.TxTransactionGroupVO;

import java.util.List;

/**
 * <p>Description: .</p>
 *
 * @author xiaoyu(Myth)
 * @version 1.0
 * @date 2017/10/18 15:38
 * @since JDK 1.8
 */
public interface TxTransactionGroupService {

    /**
     * 分页获取事务组里面的事务信息
     *
     * @param txTransactionQuery 查询条件
     * @return CommonPager<TxTransactionGroupVO>
     */
    CommonPager<TxTransactionGroupVO> listByPage(TxTransactionQuery txTransactionQuery);

    /**
     * 批量删除事务信息
     *
     * @param txGroupIdList 事务组id集合
     * @return true 成功
     */
    Boolean batchRemove(List<String> txGroupIdList);

}
