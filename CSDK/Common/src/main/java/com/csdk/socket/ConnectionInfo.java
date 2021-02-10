package com.csdk.socket;

import java.io.Serializable;

/**
 * Create LuckMerlin
 * Date 10:35 2020/12/22
 * TODO
 */
public final class ConnectionInfo implements Serializable, Cloneable {
    private String mIp;
    private int mPort;
    private ConnectionInfo mBackupInfo;

    public ConnectionInfo(String ip, int port) {
        this.mIp = ip;
        this.mPort = port;
    }

    public String getIp() {
        return this.mIp;
    }

    public int getPort() {
        return this.mPort;
    }

    public ConnectionInfo getBackupInfo() {
        return this.mBackupInfo;
    }

    public void setBackupInfo(ConnectionInfo backupInfo) {
        this.mBackupInfo = backupInfo;
    }

    public ConnectionInfo clone() {
        ConnectionInfo connectionInfo = new ConnectionInfo(this.mIp, this.mPort);
        if (this.mBackupInfo != null) {
            connectionInfo.setBackupInfo(this.mBackupInfo.clone());
        }

        return connectionInfo;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof ConnectionInfo)) {
            return false;
        } else {
            ConnectionInfo connectInfo = (ConnectionInfo)o;
            return this.mPort != connectInfo.mPort ? false : this.mIp.equals(connectInfo.mIp);
        }
    }

    public int hashCode() {
        int result = this.mIp.hashCode();
        result = 31 * result + this.mPort;
        return result;
    }
}