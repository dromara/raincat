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
package com.happylifeplat.transaction.core.concurrent.threadlocal;

/**
 * @author xiaoyu
 */
public class CompensationLocal {

    private static final CompensationLocal COMPENSATION_LOCAL = new CompensationLocal();

    private CompensationLocal() {

    }

    public static CompensationLocal getInstance() {
        return COMPENSATION_LOCAL;
    }


    private static final ThreadLocal<String> CURRENT_LOCAL = new ThreadLocal<>();


    public void setCompensationId(String compensationId) {
        CURRENT_LOCAL.set(compensationId);
    }

    public String getCompensationId() {
        return CURRENT_LOCAL.get();
    }

    public void removeCompensationId() {
        CURRENT_LOCAL.remove();
    }

}
