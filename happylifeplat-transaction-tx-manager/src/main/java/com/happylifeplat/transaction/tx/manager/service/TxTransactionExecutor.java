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
package com.happylifeplat.transaction.tx.manager.service;

/**
 * @author xiaoyu
 */
public interface TxTransactionExecutor {


    /**
     * 回滚整个事务组
     *
     * @param txGroupId 事务组id
     */
    void rollBack(String txGroupId);


    /**
     * 事务预提交
     *
     * @param txGroupId 事务组id
     * @return true 成功 false 失败
     */
    Boolean preCommit(String txGroupId);


}
