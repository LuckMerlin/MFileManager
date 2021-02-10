package com.csdk.socket;

import java.io.Serializable;

/**
 * Create LuckMerlin
 * Date 10:44 2020/12/22
 * TODO
 */
 interface IClient extends IDisConnectable, ISender<IClient>, Serializable {

    String getHostIp();

    String getHostName();

    String getUniqueTag();

    void setReaderProtocol(IReaderProtocol protocol);

    void addIOCallback(IClientIOCallback clientIOCallback);

    void removeIOCallback(IClientIOCallback clientIOCallback);

    void removeAllIOCallback();

}