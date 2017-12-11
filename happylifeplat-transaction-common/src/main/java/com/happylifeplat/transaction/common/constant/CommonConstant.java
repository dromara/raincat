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

package com.happylifeplat.transaction.common.constant;

/**
 * <p>Description: .</p>
 *
 * @author xiaoyu(Myth)
 * @version 1.0
 * @date 2017/10/18 15:36
 * @since JDK 1.8
 */
public interface CommonConstant {


    String REDIS_PRE_FIX = "transaction:group:%s";

    String REDIS_KEYS = "transaction:group:*";


    String REDIS_KEY_SET = "transaction:group";

    double REDIS_SCOPE = 10.0;

    String PATH_SUFFIX = "/tx";

    String DB_SUFFIX = "tx_";

    String RECOVER_REDIS_KEY_PRE = "transaction:recover:%s";


    String DB_MYSQL = "mysql";

    String DB_SQLSERVER = "sqlserver";

    String DB_ORACLE = "oracle";

    String COMPENSATE_KEY = "COMPENSATE";

    String COMPENSATE_ID = "COMPENSATE_ID";

    String TX_TRANSACTION_GROUP = "TX_TRANSACTION_GROUP";

    String TX_MANAGER_PRE = "/tx/manager";

    String LOAD_TX_MANAGER_SERVICE_URL = "/loadTxManagerService";

    String FIND_SERVER = "/findTxManagerServer";
}
