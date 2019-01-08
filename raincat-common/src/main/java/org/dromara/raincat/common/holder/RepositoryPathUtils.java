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
 * RepositoryPathUtils.
 * @author xiaoyu(Myth)
 */
public class RepositoryPathUtils {

    public static String buildFilePath(String applicationName) {
        return String.join("/", CommonConstant.PATH_SUFFIX, applicationName.replaceAll("-", "_"));
    }

    public static String buildDbTableName(String applicationName) {
        return CommonConstant.DB_SUFFIX + applicationName.replaceAll("-", "_");
    }

    public static String buildMongoTableName(String applicationName) {
        return CommonConstant.DB_SUFFIX + applicationName.replaceAll("-", "_");
    }

    public static String buildRedisKey(String applicationName) {
        return String.format(CommonConstant.RECOVER_REDIS_KEY_PRE, applicationName);
    }

    public static String buildZookeeperPath(String applicationName) {
        return String.join("_", CommonConstant.PATH_SUFFIX, applicationName);
    }

}
