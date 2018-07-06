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
import com.raincat.admin.dto.RecoverDTO;
import com.raincat.admin.page.CommonPager;
import com.raincat.admin.query.RecoverTransactionQuery;
import com.raincat.admin.service.RecoverApplicationNameService;
import com.raincat.admin.service.RecoverTransactionService;
import com.raincat.admin.vo.TransactionRecoverVO;
import com.raincat.common.holder.httpclient.AjaxResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * RecoverTransactionController.
 * @author xiaoyu(Myth)
 */
@RestController
@RequestMapping("/recover")
public class RecoverTransactionController {

    private final RecoverTransactionService recoverTransactionService;

    private final RecoverApplicationNameService recoverApplicationNameService;

    @Value("${recover.retry.max}")
    private Integer recoverRetryMax;

    @Autowired
    public RecoverTransactionController(final RecoverTransactionService recoverTransactionService,
                                        final RecoverApplicationNameService recoverApplicationNameService) {
        this.recoverTransactionService = recoverTransactionService;
        this.recoverApplicationNameService = recoverApplicationNameService;
    }

    @Permission
    @PostMapping(value = "/listPage")
    public AjaxResponse listPage(final @RequestBody RecoverTransactionQuery recoverQuery) {
        final CommonPager<TransactionRecoverVO> pager =
                recoverTransactionService.listByPage(recoverQuery);
        return AjaxResponse.success(pager);
    }

    @PostMapping(value = "/batchRemove")
    @Permission
    public AjaxResponse batchRemove(final @RequestBody RecoverDTO recoverDTO) {

        final Boolean success = recoverTransactionService.batchRemove(recoverDTO.getIds(), recoverDTO.getApplicationName());
        return AjaxResponse.success(success);

    }

    @PostMapping(value = "/update")
    @Permission
    public AjaxResponse update(final @RequestBody RecoverDTO recoverDTO) {
        if (recoverRetryMax < recoverDTO.getRetry()) {
            return AjaxResponse.error("重试次数超过最大设置，请您重新设置！");
        }
        final Boolean success = recoverTransactionService.updateRetry(recoverDTO.getId(),
                recoverDTO.getRetry(), recoverDTO.getApplicationName());
        return AjaxResponse.success(success);

    }

    @PostMapping(value = "/listAppName")
    @Permission
    public AjaxResponse listAppName() {
        final List<String> list = recoverApplicationNameService.list();
        return AjaxResponse.success(list);
    }

}
