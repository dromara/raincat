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
package com.happylifeplat.transaction.tx.manager.controller;

import com.happylifeplat.transaction.tx.manager.entity.TxManagerInfo;
import com.happylifeplat.transaction.tx.manager.service.TxManagerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xiaoyu
 */
@Controller
public class TxManagerIndexController {

    private final TxManagerInfoService txManagerInfoService;

    @Autowired
    public TxManagerIndexController(TxManagerInfoService txManagerInfoService) {
        this.txManagerInfoService = txManagerInfoService;
    }


    @RequestMapping("/index")
    public String index(HttpServletRequest request) {
        final TxManagerInfo txManagerInfo = txManagerInfoService.findTxManagerInfo();
        request.setAttribute("info", txManagerInfo);
        return "index";
    }


}
