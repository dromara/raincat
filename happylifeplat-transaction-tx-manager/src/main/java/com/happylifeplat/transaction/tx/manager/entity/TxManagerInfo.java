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
package com.happylifeplat.transaction.tx.manager.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author xiaoyu
 */
@Data
public class TxManagerInfo {


    private static final long serialVersionUID = 1975118058422053078L;
    /**
     * socket ip
     */
    private String ip;
    /**
     * socket port
     */
    private int port;

    /**
     * max connection
     */
    private int maxConnection;

    /**
     * now connection
     */
    private int nowConnection;

    /**
     * transaction_wait_max_time
     */
    private int transactionWaitMaxTime;

    /**
     * redis_save_max_time
     */
    private int redisSaveMaxTime;

    /**
     * clusterInfoList
     */
    private List<String> clusterInfoList;

}
