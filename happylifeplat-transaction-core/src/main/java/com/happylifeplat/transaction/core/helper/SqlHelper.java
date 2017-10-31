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

package com.happylifeplat.transaction.core.helper;

import com.happylifeplat.transaction.common.holder.DbTypeUtils;

/**
 * @author xiaoyu
 */
public class SqlHelper {

    public static String buildCreateTableSql(String tableName, String driverClassName) {
        String createTableSql;
        String dbType = DbTypeUtils.buildByDriverClassName(driverClassName);
        switch (dbType) {
            case "mysql": {
                createTableSql = "CREATE TABLE `" + tableName + "` (\n" +
                        "  `id` varchar(64) NOT NULL,\n" +
                        "  `target_class` varchar(256) ,\n" +
                        "  `target_method` varchar(128) ,\n" +
                        "  `retried_count` int(3) NOT NULL,\n" +
                        "  `create_time` datetime NOT NULL,\n" +
                        "  `last_time` datetime NOT NULL,\n" +
                        "  `version` int(6) NOT NULL,\n" +
                        "  `group_id` varchar(64) NOT NULL,\n" +
                        "  `task_id` varchar(64) NOT NULL,\n" +
                        "  `invocation` longblob NOT NULL,\n" +
                        "  PRIMARY KEY (`id`)\n" +
                        ")";
                break;
            }
            case "oracle": {
                createTableSql = "CREATE TABLE `" + tableName + "` (\n" +
                        "  `id` varchar(64) NOT NULL,\n" +
                        "  `target_class` varchar(256) ,\n" +
                        "  `target_method` varchar(128) ,\n" +
                        "  `retried_count` int(3) NOT NULL,\n" +
                        "  `create_time` date NOT NULL,\n" +
                        "  `last_time` date NOT NULL,\n" +
                        "  `version` int(6) NOT NULL,\n" +
                        "  `group_id` varchar2(64) NOT NULL,\n" +
                        "  `task_id` varchar2(64) NOT NULL,\n" +
                        "  `invocation` BLOB NOT NULL,\n" +
                        "  PRIMARY KEY (`id`)\n" +
                        ")";
                break;
            }
            case "sqlserver": {
                createTableSql = "CREATE TABLE `" + tableName + "` (\n" +
                        "  `id` varchar(64) NOT NULL,\n" +
                        "  `target_class` varchar(256) ,\n" +
                        "  `target_method` varchar(128) ,\n" +
                        "  `retried_count` int(3) NOT NULL,\n" +
                        "  `create_time` datetime NOT NULL,\n" +
                        "  `last_time` datetime NOT NULL,\n" +
                        "  `version` int(6) NOT NULL,\n" +
                        "  `group_id` nchar(64) NOT NULL,\n" +
                        "  `task_id` nchar(64) NOT NULL,\n" +
                        "  `invocation` varbinary NOT NULL,\n" +
                        "  PRIMARY KEY (`id`)\n" +
                        ")";
                break;
            }
            default: {
                throw new RuntimeException("dbType类型不支持,目前仅支持mysql oracle sqlserver.");
            }
        }
        return createTableSql;


    }
}
