package com.csdk.socket;

/**
 * Create LuckMerlin
 * Date 10:38 2020/12/22
 * TODO
 */
 abstract class SocketActionAdapter implements ISocketActionListener {
    public SocketActionAdapter() {
    }

    public void onSocketIOThreadStart(String action) {
    }

    public void onSocketIOThreadShutdown(String action, Exception e) {
    }

    public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
    }

    public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
    }

    public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
    }

    public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
    }

    public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
    }

    public void onPulseSend(ConnectionInfo info, IPulseSendable data) {
    }
}
