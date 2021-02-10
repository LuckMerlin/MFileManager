package com.csdk.socket;

/**
 * Create LuckMerlin
 * Date 10:46 2020/12/22
 * TODO
 */
interface IServerActionListener {
    void onServerListening(int serverPort);

    void onClientConnected(IClient client, int serverPort, IClientPool clientPool);

    void onClientDisconnected(IClient client, int serverPort, IClientPool clientPool);

    void onServerWillBeShutdown(int serverPort, IServerShutdown shutdown, IClientPool clientPool, Throwable throwable);

    void onServerAlreadyShutdown(int serverPort);

}
