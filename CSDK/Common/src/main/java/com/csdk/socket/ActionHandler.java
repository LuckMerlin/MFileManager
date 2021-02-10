package com.csdk.socket;

/**
 * Create LuckMerlin
 * Date 10:28 2020/12/22
 * TODO
 */
class ActionHandler extends SocketActionAdapter {
    private IConnectionManager mManager;
    private OkSocketOptions.IOThreadMode mCurrentThreadMode;
    private boolean iOThreadIsCalledDisconnect = false;

    public ActionHandler() {
    }

    public void attach(IConnectionManager manager, IRegister<ISocketActionListener, IConnectionManager> register) {
        this.mManager = manager;
        register.registerReceiver(this);
    }

    public void detach(IRegister register) {
        register.unRegisterReceiver(this);
    }

    public void onSocketIOThreadStart(String action) {
        if (this.mManager.getOption().getIOThreadMode() != this.mCurrentThreadMode) {
            this.mCurrentThreadMode = this.mManager.getOption().getIOThreadMode();
        }

        this.iOThreadIsCalledDisconnect = false;
    }

    public void onSocketIOThreadShutdown(String action, Exception e) {
        if (this.mCurrentThreadMode == this.mManager.getOption().getIOThreadMode() && !this.iOThreadIsCalledDisconnect) {
            this.iOThreadIsCalledDisconnect = true;
            if (!(e instanceof ManuallyDisconnectException)) {
                this.mManager.disconnect(e);
            }
        }

    }

    public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
        this.mManager.disconnect(e);
    }
}
