package com.happylifeplat.transaction.common.holder.httpclient;

import javax.net.ssl.TrustManagerFactory;

public class TrustKeyStore {
    private TrustManagerFactory trustManagerFactory;

    TrustKeyStore(TrustManagerFactory trustManagerFactory) {
        this.trustManagerFactory = trustManagerFactory;
    }

    TrustManagerFactory getTrustManagerFactory() {
        return trustManagerFactory;
    }
}
