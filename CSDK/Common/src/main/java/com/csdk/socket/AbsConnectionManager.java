package com.csdk.socket;

import java.io.Serializable;

/**
 * Create LuckMerlin
 * Date 10:25 2020/12/22
 * TODO
 */
 abstract class AbsConnectionManager  implements IConnectionManager {
    protected ConnectionInfo mConnectionInfo;
    private IConnectionSwitchListener mConnectionSwitchListener;
    protected ActionDispatcher mActionDispatcher;

    public AbsConnectionManager(ConnectionInfo info) {
        this.mConnectionInfo = info;
        this.mActionDispatcher = new ActionDispatcher(info, this);
    }

    public IConnectionManager registerReceiver(ISocketActionListener socketResponseHandler) {
        this.mActionDispatcher.registerReceiver(socketResponseHandler);
        return this;
    }

    public IConnectionManager unRegisterReceiver(ISocketActionListener socketResponseHandler) {
        this.mActionDispatcher.unRegisterReceiver(socketResponseHandler);
        return this;
    }

    protected void sendBroadcast(String action, Serializable serializable) {
        this.mActionDispatcher.sendBroadcast(action, serializable);
    }

    protected void sendBroadcast(String action) {
        this.mActionDispatcher.sendBroadcast(action);
    }

    public ConnectionInfo getConnectionInfo() {
        return this.mConnectionInfo != null ? this.mConnectionInfo.clone() : null;
    }

    public synchronized void switchConnectionInfo(ConnectionInfo info) {
        if (info != null) {
            ConnectionInfo tempOldInfo = this.mConnectionInfo;
            this.mConnectionInfo = info.clone();
            if (this.mActionDispatcher != null) {
                this.mActionDispatcher.setConnectionInfo(this.mConnectionInfo);
            }

            if (this.mConnectionSwitchListener != null) {
                this.mConnectionSwitchListener.onSwitchConnectionInfo(this, tempOldInfo, this.mConnectionInfo);
            }
        }

    }

    protected void setOnConnectionSwitchListener(IConnectionSwitchListener listener) {
        this.mConnectionSwitchListener = listener;
    }
}