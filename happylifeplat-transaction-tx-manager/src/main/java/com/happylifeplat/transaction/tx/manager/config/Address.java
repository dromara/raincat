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
package com.happylifeplat.transaction.tx.manager.config;

import lombok.Data;

/**
 * @author xiaoyu
 */
public class Address {
    private static final Address OUR_INSTANCE = new Address();

    /**
     * 自身的ip
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;


    /**
     * 域名  ip：port
     */
    private String domain;


    public static Address getInstance() {
        return OUR_INSTANCE;
    }

    private Address() {
    }


    public String getHost() {
        return host;
    }

    public Address setHost(String host) {
        this.host = host;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public Address setPort(Integer port) {
        this.port = port;
        return this;
    }

    public String getDomain() {
        return domain;
    }

    public Address setDomain(String domain) {
        this.domain = domain;
        return this;
    }
}
