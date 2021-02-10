package com.csdk.socket;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * Create LuckMerlin
 * Date 10:37 2020/12/22
 * TODO
 */
class OkSocketSSLConfig {
    private String mProtocol;
    private TrustManager[] mTrustManagers;
    private KeyManager[] mKeyManagers;
    private SSLSocketFactory mCustomSSLFactory;

    private OkSocketSSLConfig() {
    }

    public KeyManager[] getKeyManagers() {
        return this.mKeyManagers;
    }

    public String getProtocol() {
        return this.mProtocol;
    }

    public TrustManager[] getTrustManagers() {
        return this.mTrustManagers;
    }

    public SSLSocketFactory getCustomSSLFactory() {
        return this.mCustomSSLFactory;
    }

    public static class Builder {
        private OkSocketSSLConfig mConfig = new OkSocketSSLConfig();

        public Builder() {
        }

        public Builder setProtocol(String protocol) {
            this.mConfig.mProtocol = protocol;
            return this;
        }

        public Builder setTrustManagers(TrustManager[] trustManagers) {
            this.mConfig.mTrustManagers = trustManagers;
            return this;
        }

        public Builder setKeyManagers(KeyManager[] keyManagers) {
            this.mConfig.mKeyManagers = keyManagers;
            return this;
        }

        public Builder setCustomSSLFactory(SSLSocketFactory customSSLFactory) {
            this.mConfig.mCustomSSLFactory = customSSLFactory;
            return this;
        }

        public OkSocketSSLConfig build() {
            return this.mConfig;
        }
    }
}
