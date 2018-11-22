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

package org.dromara.raincat.manager.controller;

import org.dromara.raincat.common.entity.TxManagerServer;
import org.dromara.raincat.common.entity.TxManagerServiceDTO;
import org.dromara.raincat.common.netty.bean.TxTransactionItem;
import org.dromara.raincat.manager.entity.TxManagerInfo;
import org.dromara.raincat.manager.service.TxManagerInfoService;
import org.dromara.raincat.manager.service.execute.HttpTransactionExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * TxManagerController.
 * @author xiaoyu
 */
@RestController
@RequestMapping("/tx/manager")
public class TxManagerController {

    private final TxManagerInfoService txManagerInfoService;

    private final HttpTransactionExecutor httpTransactionExecutor;

    @Autowired
    public TxManagerController(final TxManagerInfoService txManagerInfoService,
                               final HttpTransactionExecutor transactionExecutor) {
        this.txManagerInfoService = txManagerInfoService;
        this.httpTransactionExecutor = transactionExecutor;
    }

    @ResponseBody
    @PostMapping("/findTxManagerServer")
    public TxManagerServer findTxManagerServer() {
        return txManagerInfoService.findTxManagerServer();
    }

    @ResponseBody
    @PostMapping("/loadTxManagerService")
    public List<TxManagerServiceDTO> loadTxManagerService() {
        return txManagerInfoService.loadTxManagerService();
    }

    @RequestMapping("/findTxManagerInfo")
    public TxManagerInfo findTxManagerInfo() {
        return txManagerInfoService.findTxManagerInfo();
    }

    @PostMapping("/httpCommit")
    public void httpCommit(@RequestBody final List<TxTransactionItem> items) {
        httpTransactionExecutor.commit(items);
    }

    @PostMapping("/httpRollBack")
    public void httpRollBack(@RequestBody final List<TxTransactionItem> items) {
        httpTransactionExecutor.rollBack(items);
    }

}
