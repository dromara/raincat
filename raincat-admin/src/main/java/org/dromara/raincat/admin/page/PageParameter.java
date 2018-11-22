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

package org.dromara.raincat.admin.page;

import lombok.Data;

import java.io.Serializable;

/**
 * PageParameter.
 * @author xiaoyu(Myth)
 */
@Data
public class PageParameter implements Serializable {

    public static final int DEFAULT_PAGE_SIZE = 10;

    private static final long serialVersionUID = -8324693985921606090L;

    private int pageSize;

    private int currentPage;

    private int prePage;

    private int nextPage;

    private int totalPage;

    private int totalCount;

    public PageParameter() {
        this.currentPage = 1;
        this.pageSize = DEFAULT_PAGE_SIZE;
    }

    public PageParameter(final int currentPage, final int pageSize) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

}
