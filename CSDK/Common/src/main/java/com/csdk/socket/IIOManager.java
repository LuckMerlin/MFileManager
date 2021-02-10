package com.csdk.socket;

/**
 * Create LuckMerlin
 * Date 10:42 2020/12/22
 * TODO
 */
interface IIOManager <E extends IIOCoreOptions> {
    void startEngine();

    void setOkOptions(E options);

    void send(ISendable sendable);

    void close();

    void close(Exception e);

}
