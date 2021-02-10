package com.csdk.socket;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Create LuckMerlin
 * Date 10:39 2020/12/22
 * TODO
 */
abstract class AbsReconnectionManager implements ISocketActionListener {
    protected volatile IConnectionManager mConnectionManager;
    protected PulseManager mPulseManager;
    protected volatile boolean mDetach;
    protected volatile Set<Class<? extends Exception>> mIgnoreDisconnectExceptionList = new LinkedHashSet();

    public AbsReconnectionManager() {
    }

    public synchronized void attach(IConnectionManager manager) {
        if (this.mDetach) {
            this.detach();
        }

        this.mDetach = false;
        this.mConnectionManager = manager;
        this.mPulseManager = manager.getPulseManager();
        this.mConnectionManager.registerReceiver(this);
    }

    public synchronized void detach() {
        this.mDetach = true;
        if (this.mConnectionManager != null) {
            this.mConnectionManager.unRegisterReceiver(this);
        }

    }

    public final void addIgnoreException(Class<? extends Exception> e) {
        synchronized(this.mIgnoreDisconnectExceptionList) {
            this.mIgnoreDisconnectExceptionList.add(e);
        }
    }

    public final void removeIgnoreException(Exception e) {
        synchronized(this.mIgnoreDisconnectExceptionList) {
            this.mIgnoreDisconnectExceptionList.remove(e.getClass());
        }
    }

    public final void removeIgnoreException(Class<? extends Exception> e) {
        synchronized(this.mIgnoreDisconnectExceptionList) {
            this.mIgnoreDisconnectExceptionList.remove(e);
        }
    }

    public final void removeAll() {
        synchronized(this.mIgnoreDisconnectExceptionList) {
            this.mIgnoreDisconnectExceptionList.clear();
        }
    }

    public void onSocketIOThreadStart(String action) {
    }

    public void onSocketIOThreadShutdown(String action, Exception e) {
    }

    public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
    }

    public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
    }

    public void onPulseSend(ConnectionInfo info, IPulseSendable data) {
    }
}
