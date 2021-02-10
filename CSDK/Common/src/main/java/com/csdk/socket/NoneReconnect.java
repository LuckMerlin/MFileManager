package com.csdk.socket;

/**
 * Create LuckMerlin
 * Date 10:41 2020/12/22
 * TODO
 */
class NoneReconnect extends AbsReconnectionManager {
    public NoneReconnect() {
    }

    public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
    }

    public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
    }

    public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
    }
}
