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

package org.dromara.raincat.common.holder;

import org.dromara.raincat.common.constant.CommonConstant;

/**
 * DbTypeUtils.
 * @author xiaoyu(Myth)
 */
public class DbTypeUtils {

    /**
     * 判断是什么类型的数据库.
     * @param driverClassName 驱动名称
     * @return mysql sqlserver oracle .
     */
    public static String buildByDriverClassName(final String driverClassName) {
        String dbType = "mysql";
        if (driverClassName.contains(CommonConstant.DB_MYSQL)) {
            dbType = "mysql";
        } else if (driverClassName.contains(CommonConstant.DB_SQLSERVER)) {
            dbType = "sqlserver";
        } else if (driverClassName.contains(CommonConstant.DB_ORACLE)) {
            dbType = "oracle";
        }
        return dbType;
    }

}
