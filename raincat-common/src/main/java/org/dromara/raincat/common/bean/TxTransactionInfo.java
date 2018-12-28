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

package org.dromara.raincat.common.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dromara.raincat.common.enums.PropagationEnum;

/**
 * TxTransactionInfo.
 * @author xiaoyu
 */
@AllArgsConstructor
public class TxTransactionInfo {

    /**
     * 补偿方法对象.
     */
    @Getter
    private TransactionInvocation invocation;

    /**
     * 分布式事务组.
     */
    @Getter
    private String txGroupId;

    /**
     * 事务补偿id.
     */
    @Getter
    private String compensationId;

    /**
     * 事务等待时间.
     */
    @Getter
    private int waitMaxTime;

    @Getter
    private PropagationEnum propagationEnum;

    /**
     * 事务管理器名称
     */
    @Getter
    private String transactionManager;


}
