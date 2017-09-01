package com.happylifeplat.transaction.tx.springcloud.http;

import com.happylifeplat.transaction.core.concurrent.threadlocal.TxTransactionLocal;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * HttpRequestInterceptor
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/11 18:22
 * @since JDK 1.8
 */
public class HttpRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().add("tx-group", TxTransactionLocal.getInstance().getTxGroupId());
        return execution.execute(request,body);
    }
}
