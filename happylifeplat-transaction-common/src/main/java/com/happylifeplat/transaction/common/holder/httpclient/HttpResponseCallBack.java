package com.happylifeplat.transaction.common.holder.httpclient;

import java.io.IOException;
import java.io.InputStream;

public interface HttpResponseCallBack {

    void processResponse(InputStream responseBody) throws IOException;
}
