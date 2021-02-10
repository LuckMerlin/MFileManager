package com.csdk.socket;

/**
 * Create LuckMerlin
 * Date 10:46 2020/12/22
 * TODO
 */
 interface IServerManager <E extends IIOCoreOptions> extends IServerShutdown {

    void listen();

    void listen(E options);

    boolean isLive();

    IClientPool<String, IClient> getClientPool();
}

