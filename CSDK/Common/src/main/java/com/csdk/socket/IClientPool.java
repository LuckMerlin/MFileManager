package com.csdk.socket;

/**
 * Create LuckMerlin
 * Date 10:45 2020/12/22
 * TODO
 */
interface IClientPool<T, K> {

    void cache(T t);

    T findByUniqueTag(K key);

    int size();

    void sendToAll(ISendable sendable);
}
