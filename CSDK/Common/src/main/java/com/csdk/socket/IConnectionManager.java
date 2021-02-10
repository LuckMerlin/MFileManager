package com.csdk.socket;

/**
 * Create LuckMerlin
 * Date 10:40 2020/12/22
 * TODO
 */
public interface IConnectionManager extends IConfiguration, IConnectable, IDisConnectable, ISender<IConnectionManager>, IRegister<ISocketActionListener, IConnectionManager> {
    boolean isConnect();

    boolean isDisconnecting();

    PulseManager getPulseManager();

    void setIsConnectionHolder(boolean var1);

    ConnectionInfo getConnectionInfo();

    void switchConnectionInfo(ConnectionInfo var1);

    AbsReconnectionManager getReconnectionManager();
}
