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

package com.raincat.motan.filter;

import com.raincat.common.constant.CommonConstant;
import com.raincat.core.concurrent.threadlocal.TxTransactionLocal;
import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.core.extension.Activation;
import com.weibo.api.motan.core.extension.Scope;
import com.weibo.api.motan.core.extension.Spi;
import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.filter.Filter;
import com.weibo.api.motan.rpc.Caller;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;


/**
 * MotanTxTransactionFilter.
 * @author xiaoyu
 */
@Spi(scope = Scope.SINGLETON)
@SpiMeta(name = "motanTxTransactionFilter")
@Activation(key = {MotanConstants.NODE_TYPE_REFERER})
public class MotanTxTransactionFilter implements Filter {

    @Override
    public Response filter(final Caller<?> caller, final Request request) {
        request.setAttachment(CommonConstant.TX_TRANSACTION_GROUP, TxTransactionLocal.getInstance().getTxGroupId());
        return caller.call(request);
    }
}
