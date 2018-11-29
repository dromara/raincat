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

package org.dromara.raincat.common.constant;

/**
 * CommonConstant.
 *
 * @author xiaoyu(Myth)
 */
public interface CommonConstant {

    /**
     * The constant REDIS_PRE_FIX.
     */
    String REDIS_PRE_FIX = "transaction:group:%s";

    /**
     * The constant REDIS_KEYS.
     */
    String REDIS_KEYS = "transaction:group:*";

    /**
     * The constant REDIS_KEY_SET.
     */
    String REDIS_KEY_SET = "transaction:group";

    /**
     * The constant REDIS_SCOPE.
     */
    double REDIS_SCOPE = 10.0;

    /**
     * The constant PATH_SUFFIX.
     */
    String PATH_SUFFIX = "/tx";

    /**
     * The constant DB_SUFFIX.
     */
    String DB_SUFFIX = "tx_";

    /**
     * The constant RECOVER_REDIS_KEY_PRE.
     */
    String RECOVER_REDIS_KEY_PRE = "transaction:recover:%s";

    /**
     * The constant DB_MYSQL.
     */
    String DB_MYSQL = "mysql";

    /**
     * The constant DB_SQLSERVER.
     */
    String DB_SQLSERVER = "sqlserver";

    /**
     * The constant DB_ORACLE.
     */
    String DB_ORACLE = "oracle";

    /**
     * The constant COMPENSATE_KEY.
     */
    String COMPENSATE_KEY = "COMPENSATE";

    /**
     * The constant COMPENSATE_ID.
     */
    String COMPENSATE_ID = "COMPENSATE_ID";

    /**
     * The constant TX_TRANSACTION_GROUP.
     */
    String TX_TRANSACTION_GROUP = "Tx-Transaction-Group";

    /**
     * The constant TX_MANAGER_PRE.
     */
    String TX_MANAGER_PRE = "/tx/manager";

    /**
     * The constant LOAD_TX_MANAGER_SERVICE_URL.
     */
    String LOAD_TX_MANAGER_SERVICE_URL = "/loadTxManagerService";

    /**
     * The constant FIND_SERVER.
     */
    String FIND_SERVER = "/findTxManagerServer";


    /**
     * The constant TX_TRANSACTION_COMMIT_STATUS_BAD.
     */
    String TX_TRANSACTION_COMMIT_STATUS_BAD = "0";

    /**
     * The constant TX_TRANSACTION_COMMIT_STATUS_OK.
     */
    String TX_TRANSACTION_COMMIT_STATUS_OK = "1";

    /**
     * The constant TX_TRANSACTION_COMPLETE_FLAG_BAD.
     */
    String TX_TRANSACTION_COMPLETE_FLAG_BAD = "0";

    /**
     * The constant TX_TRANSACTION_COMPLETE_FLAG_OK.
     */
    String TX_TRANSACTION_COMPLETE_FLAG_OK = "1";

    /**
     * The constant LINE_SEPARATOR.
     */
    String LINE_SEPARATOR = System.getProperty("line.separator");


}
