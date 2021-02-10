package com.csdk.socket;

/**
 * Create LuckMerlin
 * Date 10:45 2020/12/22
 * TODO
 */
interface IClientIOCallback {

    void onClientRead(OriginalData originalData, IClient client, IClientPool<IClient, String> clientPool);

    void onClientWrite(ISendable sendable, IClient client, IClientPool<IClient, String> clientPool);

}
