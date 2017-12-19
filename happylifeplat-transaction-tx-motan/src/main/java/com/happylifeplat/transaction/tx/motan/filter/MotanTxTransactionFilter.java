package com.happylifeplat.transaction.tx.motan.filter;

import com.happylifeplat.transaction.common.constant.CommonConstant;
import com.happylifeplat.transaction.core.concurrent.threadlocal.TxTransactionLocal;
import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.core.extension.Activation;
import com.weibo.api.motan.core.extension.Scope;
import com.weibo.api.motan.core.extension.Spi;
import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.filter.Filter;
import com.weibo.api.motan.rpc.Caller;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;
import com.weibo.api.motan.rpc.RpcContext;


/**
 * @author xiaoyu
 */
@Spi(scope = Scope.SINGLETON)
@SpiMeta(name = "motanTxTransactionFilter")
@Activation(key = {MotanConstants.NODE_TYPE_REFERER})
public class MotanTxTransactionFilter implements Filter {

    /**
     * 实现新浪的filter接口 rpc传参数
     * @param caller caller
     * @param request 请求
     * @return Response
     */
    @Override
    public Response filter(Caller<?> caller, Request request) {
        request.setAttachment(CommonConstant.TX_TRANSACTION_GROUP,
                TxTransactionLocal.getInstance().getTxGroupId());
        return caller.call(request);
    }
}
