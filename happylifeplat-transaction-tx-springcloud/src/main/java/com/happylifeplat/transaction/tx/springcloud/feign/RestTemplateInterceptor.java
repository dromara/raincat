package com.happylifeplat.transaction.tx.springcloud.feign;

import com.happylifeplat.transaction.core.concurrent.threadlocal.TxTransactionLocal;
import feign.RequestInterceptor;
import feign.RequestTemplate;


/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * RestTemplateInterceptor
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/11 18:22
 * @since JDK 1.8
 */
public class RestTemplateInterceptor implements RequestInterceptor {


    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("tx-group",  TxTransactionLocal.getInstance().getTxGroupId());
    }

}
