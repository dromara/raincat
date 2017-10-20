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

package com.happylifeplat.transaction.admin.controller;

import com.happylifeplat.transaction.admin.page.CommonPager;
import com.happylifeplat.transaction.admin.page.PageParameter;
import com.happylifeplat.transaction.admin.query.TxTransactionQuery;
import com.happylifeplat.transaction.admin.service.TxTransactionItemService;
import com.happylifeplat.transaction.admin.vo.TxTransactionItemVO;
import com.happylifeplat.transaction.common.holder.httpclient.AjaxResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Description: .</p>
 * 查询TxManager存储的事务item信息
 *
 * @author xiaoyu(Myth)
 * @version 1.0
 * @date 2017/10/18 10:31
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/tx")
public class TxTransactionItemController {


    private final TxTransactionItemService txTransactionItemService;

    @Autowired
    public TxTransactionItemController(TxTransactionItemService txTransactionItemService) {
        this.txTransactionItemService = txTransactionItemService;
    }


    @RequestMapping(value = "/listPage", method = RequestMethod.POST)
    public AjaxResponse listPage(@RequestBody TxTransactionQuery txTransactionQuery) {
        final CommonPager<TxTransactionItemVO> commonPager =
                txTransactionItemService.listByPage(txTransactionQuery);
        return AjaxResponse.success(commonPager);
    }


}
