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

package org.dromara.raincat.admin.service;

import org.dromara.raincat.admin.page.CommonPager;
import org.dromara.raincat.admin.query.RecoverTransactionQuery;
import org.dromara.raincat.admin.vo.TransactionRecoverVO;

import java.util.List;

/**
 * RecoverTransactionService.
 * @author xiaoyu(Myth)
 */
public interface RecoverTransactionService {


    /**
     * 分页获取补偿事务信息.
     *
     * @param query 查询条件
     * @return CommonPager TransactionRecoverVO
     */
    CommonPager<TransactionRecoverVO> listByPage(RecoverTransactionQuery query);


    /**
     * 批量删除补偿事务信息.
     *
     * @param ids             ids 事务id集合
     * @param applicationName 应用名称
     * @return true 成功
     */
    Boolean batchRemove(List<String> ids, String applicationName);


    /**
     * 更改恢复次数.
     *
     * @param id              事务id
     * @param retry           恢复次数
     * @param applicationName 应用名称
     * @return true 成功
     */
    Boolean updateRetry(String id, Integer retry, String applicationName);
}
