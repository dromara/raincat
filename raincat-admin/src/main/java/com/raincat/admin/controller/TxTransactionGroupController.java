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

package com.raincat.admin.controller;

import com.raincat.admin.annotation.Permission;
import com.raincat.admin.page.CommonPager;
import com.raincat.admin.query.TxTransactionQuery;
import com.raincat.admin.service.TxTransactionGroupService;
import com.raincat.admin.vo.TxTransactionGroupVO;
import com.raincat.common.holder.httpclient.AjaxResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * TxTransactionGroupController.
 * @author xiaoyu(Myth)
 */
@RestController
@RequestMapping("/tx")
public class TxTransactionGroupController {

    private final TxTransactionGroupService txTransactionGroupService;

    @Autowired
    public TxTransactionGroupController(final TxTransactionGroupService txTransactionGroupService) {
        this.txTransactionGroupService = txTransactionGroupService;
    }

    @Permission
    @PostMapping(value = "/listPage")
    public AjaxResponse listPage(final @RequestBody TxTransactionQuery txTransactionQuery) {
        final CommonPager<TxTransactionGroupVO> commonPager =
                txTransactionGroupService.listByPage(txTransactionQuery);
        return AjaxResponse.success(commonPager);
    }

    @Permission
    @PostMapping(value = "/batchRemove")
    public AjaxResponse batchRemove(final @RequestBody List<String> txGroupIds) {
        return AjaxResponse.success(txTransactionGroupService.batchRemove(txGroupIds));
    }

}
