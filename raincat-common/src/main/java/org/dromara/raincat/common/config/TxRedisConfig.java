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

package org.dromara.raincat.common.config;

import lombok.Data;

/**
 * TxRedisConfig.
 * @author xiaoyu
 */
@Data
public class TxRedisConfig {

    private Boolean cluster = false;

    private Boolean sentinel = false;

    /**
     * cluster url example:ip:port;ip:port.
     */
    private String clusterUrl;

    /**
     * sentinel url example:ip:port;ip:port.
     */
    private String sentinelUrl;

    private String masterName;

    private String hostName;

    private int port;

    private String password;

    private int maxTotal = 8;

    private int maxIdle = 8;

    private int minIdle;

    private long maxWaitMillis = -1L;

    private long minEvictableIdleTimeMillis = 1800000L;

    private long softMinEvictableIdleTimeMillis = 1800000L;

    private int numTestsPerEvictionRun = 3;

    private Boolean testOnCreate = false;

    private Boolean testOnBorrow = false;

    private Boolean testOnReturn = false;

    private Boolean testWhileIdle = false;

    private long timeBetweenEvictionRunsMillis = -1L;

    private Boolean blockWhenExhausted = true;

    private int timeOut = 10000;

}


