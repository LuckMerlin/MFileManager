package com.csdk.api.bean;

/**
 * Create LuckMerlin
 * Date 15:24 2020/9/28
 * TODO
 */
public final class ChatConfig {
    private String mProductId;
    private String mProductKey;
    private int mInterval;
    private String mServerHost;
    private String mHttpHost;
    private int mHttpPort;
    private int mServerPort;
    private int[] mAccepts;
    private int mDispatchFriendRelation;

    public ChatConfig setInterval(int interval) {
        this.mInterval = interval;
        return this;
    }

    public ChatConfig setProductId(String productId) {
        this.mProductId = productId;
        return this;
    }

    /**
     * @deprecated
     */
    public ChatConfig setServerHost(String serverHost) {
        this.mServerHost = serverHost;
        return this;
    }

    /**
     * @deprecated
     */
    public ChatConfig setServerPort(int serverPort) {
        this.mServerPort = serverPort;
        return this;
    }

    public ChatConfig setSocketHost(String serverHost,int serverPort){
        this.mServerHost=serverHost;
        this.mServerPort=serverPort;
        return this;
    }
    /**
     * @deprecated
     */
    public ChatConfig setDispatchFriendRelation(int dispatchFriendRelation) {
        this.mDispatchFriendRelation = dispatchFriendRelation;
        return this;
    }

    public int getDispatchFriendRelation() {
        return mDispatchFriendRelation;
    }

    public ChatConfig setHttpHost(String httpHost, int httpPort){
        mHttpHost=httpHost;
        mHttpPort=httpPort;
        return this;
    }

    public ChatConfig setAccepts(int ...accepts) {
        this.mAccepts = mAccepts;
        return this;
    }

    public ChatConfig setProductKey(String productKey) {
        this.mProductKey = productKey;
        return this;
    }

    public String getProductKey() {
        return mProductKey;
    }

    public int getInterval() {
        return mInterval;
    }

    public String getProductId() {
        return mProductId;
    }

    public int[] getAccepts() {
        return mAccepts;
    }

    public String getServerHost() {
        return mServerHost;
    }

    public int getHttpPort() {
        return mHttpPort;
    }

    public String getHttpHost() {
        return mHttpHost;
    }

    public int getServerPort() {
        return mServerPort;
    }

}
