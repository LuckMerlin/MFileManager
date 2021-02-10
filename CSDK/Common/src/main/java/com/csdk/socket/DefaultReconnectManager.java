package com.csdk.socket;

import java.util.Iterator;

/**
 * Create LuckMerlin
 * Date 10:40 2020/12/22
 * TODO
 */
class DefaultReconnectManager extends AbsReconnectionManager {
    private static final int MAX_CONNECTION_FAILED_TIMES = 12;
    private int mConnectionFailedTimes = 0;
    private volatile ReconnectTestingThread mReconnectTestingThread = new ReconnectTestingThread();

    public DefaultReconnectManager() {
    }

    public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
        if (this.isNeedReconnect(e)) {
            this.reconnectDelay();
        } else {
            this.resetThread();
        }

    }

    public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
        this.resetThread();
    }

    public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
        if (e != null) {
            ++this.mConnectionFailedTimes;
            if (this.mConnectionFailedTimes > 12) {
                this.resetThread();
                ConnectionInfo originInfo = this.mConnectionManager.getConnectionInfo();
                ConnectionInfo backupInfo = originInfo.getBackupInfo();
                if (backupInfo != null) {
                    ConnectionInfo bbInfo = new ConnectionInfo(originInfo.getIp(), originInfo.getPort());
                    backupInfo.setBackupInfo(bbInfo);
                    if (!this.mConnectionManager.isConnect()) {
                        SLog.i("Prepare switch to the backup line " + backupInfo.getIp() + ":" + backupInfo.getPort() + " ...");
                        synchronized(this.mConnectionManager) {
                            this.mConnectionManager.switchConnectionInfo(backupInfo);
                        }

                        this.reconnectDelay();
                    }
                } else {
                    this.reconnectDelay();
                }
            } else {
                this.reconnectDelay();
            }
        }

    }

    private boolean isNeedReconnect(Exception e) {
        synchronized(this.mIgnoreDisconnectExceptionList) {
            if (e != null && !(e instanceof ManuallyDisconnectException)) {
                Iterator it = this.mIgnoreDisconnectExceptionList.iterator();

                Class classException;
                do {
                    if (!it.hasNext()) {
                        return true;
                    }

                    classException = (Class)it.next();
                } while(!classException.isAssignableFrom(e.getClass()));

                return false;
            } else {
                return false;
            }
        }
    }

    private synchronized void resetThread() {
        if (this.mReconnectTestingThread != null) {
            this.mReconnectTestingThread.shutdown();
        }

    }

    private void reconnectDelay() {
        synchronized(this.mReconnectTestingThread) {
            if (this.mReconnectTestingThread.isShutdown()) {
                this.mReconnectTestingThread.start();
            }

        }
    }

    public void detach() {
        super.detach();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else {
            return o != null && this.getClass() == o.getClass();
        }
    }

    private class ReconnectTestingThread extends AbsLoopThread {
        private long mReconnectTimeDelay;

        private ReconnectTestingThread() {
            this.mReconnectTimeDelay = 10000L;
        }

        protected void beforeLoop() throws Exception {
            super.beforeLoop();
            if (this.mReconnectTimeDelay < (long)(DefaultReconnectManager.this.mConnectionManager.getOption().getConnectTimeoutSecond() * 1000)) {
                this.mReconnectTimeDelay = (long)(DefaultReconnectManager.this.mConnectionManager.getOption().getConnectTimeoutSecond() * 1000);
            }

        }

        protected void runInLoopThread() throws Exception {
            if (DefaultReconnectManager.this.mDetach) {
                SLog.i("ReconnectionManager already detached by framework.We decide gave up this reconnection mission!");
                this.shutdown();
            } else {
                SLog.i("Reconnect after " + this.mReconnectTimeDelay + " mills ...");
                ThreadUtils.sleep(this.mReconnectTimeDelay);
                if (DefaultReconnectManager.this.mDetach) {
                    SLog.i("ReconnectionManager already detached by framework.We decide gave up this reconnection mission!");
                    this.shutdown();
                } else if (DefaultReconnectManager.this.mConnectionManager.isConnect()) {
                    this.shutdown();
                } else {
                    boolean isHolden = DefaultReconnectManager.this.mConnectionManager.getOption().isConnectionHolden();
                    if (!isHolden) {
                        DefaultReconnectManager.this.detach();
                        this.shutdown();
                    } else {
                        ConnectionInfo info = DefaultReconnectManager.this.mConnectionManager.getConnectionInfo();
                        SLog.i("Reconnect the server " + info.getIp() + ":" + info.getPort() + " ...");
                        synchronized(DefaultReconnectManager.this.mConnectionManager) {
                            if (!DefaultReconnectManager.this.mConnectionManager.isConnect()) {
                                DefaultReconnectManager.this.mConnectionManager.connect();
                            } else {
                                this.shutdown();
                            }

                        }
                    }
                }
            }
        }

        protected void loopFinish(Exception e) {
        }
    }
}

