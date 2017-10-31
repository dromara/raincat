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

package com.happylifeplat.transaction.admin.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Description: .</p>
 *  TxTransactionGroupVO
 * @author xiaoyu(Myth)
 * @version 1.0
 * @date 2017/10/25 10:12
 * @since JDK 1.8
 */
@Data
public class TxTransactionGroupVO implements Serializable {

    private static final long serialVersionUID = -7648437787462449972L;
    /**
     * 事务组id
     */
    private String id;

    /**
     * 事务组状态
     */
    private String status;

    /**
     * 角色
     */
    private String role;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 执行类名称
     */
    private String targetClass;
    /**
     * 执行方法
     */
    private String targetMethod;

    /**
     * 耗时 秒
     */
    private Long consumeTime;

    /**
     * 事务项集合
     */
    private List<TxTransactionItemVO> itemVOList;

}
