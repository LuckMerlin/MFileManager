package com.csdk.socket;

/**
 * Create LuckMerlin
 * Date 10:38 2020/12/22
 * TODO
 */
public interface ISocketActionListener {
    void onSocketIOThreadStart(String var1);

    void onSocketIOThreadShutdown(String var1, Exception var2);

    void onSocketReadResponse(ConnectionInfo var1, String var2, OriginalData var3);

    void onSocketWriteResponse(ConnectionInfo var1, String var2, ISendable var3);

    void onPulseSend(ConnectionInfo var1, IPulseSendable var2);

    void onSocketDisconnection(ConnectionInfo var1, String var2, Exception var3);

    void onSocketConnectionSuccess(ConnectionInfo var1, String var2);

    void onSocketConnectionFailed(ConnectionInfo var1, String var2, Exception var3);
}
