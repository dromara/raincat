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
package com.happylifeplat.transaction.common.netty.bean;

import lombok.Data;

import java.io.Serializable;


/**
 * @author xiaoyu
 */
@Data
public class TxTransactionItem implements Serializable {

    private static final long serialVersionUID = -983809184773470584L;
    /**
     * taskKey
     */
    private String taskKey;

    /**
     * 参与事务id
     */
    private String transId;

    /**
     * 事务状态 {@linkplain com.happylifeplat.transaction.common.enums.TransactionStatusEnum}
     */
    private int status;

    /**
     * 事务角色 {@linkplain com.happylifeplat.transaction.common.enums.TransactionRoleEnum}
     */
    private int role;

    /**
     * 模块信息
     */
    private String modelName;

    /**
     * tm 的域名信息
     */
    private String tmDomain;


    /**
     * 存放事务组id
     */
    private String txGroupId;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 事务最大等待时间 单位秒
     */
    private Integer waitMaxTime;


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
     * 操作结果信息
     */
    private Object message;


}
