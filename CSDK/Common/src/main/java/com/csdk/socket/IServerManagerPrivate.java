package com.csdk.socket;

/**
 * Create LuckMerlin
 * Date 10:46 2020/12/22
 * TODO
 */
interface IServerManagerPrivate<E extends IIOCoreOptions> extends IServerManager<E> {
    void initServerPrivate(int serverPort);
}

