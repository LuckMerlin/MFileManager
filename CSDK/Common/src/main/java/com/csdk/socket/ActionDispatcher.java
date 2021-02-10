package com.csdk.socket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Create LuckMerlin
 * Date 10:28 2020/12/22
 * TODO
 */
 class ActionDispatcher implements IRegister<ISocketActionListener, IConnectionManager>, IStateSender {
    private static final DispatchThread HANDLE_THREAD = new DispatchThread();
    private static final LinkedBlockingQueue<ActionBean> ACTION_QUEUE = new LinkedBlockingQueue();
    private volatile Vector<ISocketActionListener> mResponseHandlerList = new Vector();
    private volatile ConnectionInfo mConnectionInfo;
    private volatile IConnectionManager mManager;

    public ActionDispatcher(ConnectionInfo info, IConnectionManager manager) {
        this.mManager = manager;
        this.mConnectionInfo = info;
    }

    public IConnectionManager registerReceiver(ISocketActionListener socketResponseHandler) {
        if (socketResponseHandler != null) {
            synchronized(this.mResponseHandlerList) {
                if (!this.mResponseHandlerList.contains(socketResponseHandler)) {
                    this.mResponseHandlerList.add(socketResponseHandler);
                }
            }
        }

        return this.mManager;
    }

    public IConnectionManager unRegisterReceiver(ISocketActionListener socketResponseHandler) {
        this.mResponseHandlerList.remove(socketResponseHandler);
        return this.mManager;
    }

    private void dispatchActionToListener(String action, Serializable arg, ISocketActionListener responseHandler) {
        byte var5 = -1;
        switch(action.hashCode()) {
            case -1455248519:
                if (action.equals("action_read_complete")) {
                    var5 = 3;
                }
                break;
            case -1321574355:
                if (action.equals("action_read_thread_start")) {
                    var5 = 4;
                }
                break;
            case -1245920523:
                if (action.equals("action_connection_failed")) {
                    var5 = 1;
                }
                break;
            case -1201839197:
                if (action.equals("action_disconnection")) {
                    var5 = 2;
                }
                break;
            case -1121297674:
                if (action.equals("action_write_thread_start")) {
                    var5 = 5;
                }
                break;
            case -749410229:
                if (action.equals("action_connection_success")) {
                    var5 = 0;
                }
                break;
            case -542453077:
                if (action.equals("action_read_thread_shutdown")) {
                    var5 = 8;
                }
                break;
            case 190576450:
                if (action.equals("action_write_thread_shutdown")) {
                    var5 = 7;
                }
                break;
            case 1756120480:
                if (action.equals("action_pulse_request")) {
                    var5 = 9;
                }
                break;
            case 2146005698:
                if (action.equals("action_write_complete")) {
                    var5 = 6;
                }
        }

        Exception exception;
        switch(var5) {
            case 0:
                try {
                    responseHandler.onSocketConnectionSuccess(this.mConnectionInfo, action);
                } catch (Exception var14) {
                    var14.printStackTrace();
                }
                break;
            case 1:
                try {
                    exception = (Exception)arg;
                    responseHandler.onSocketConnectionFailed(this.mConnectionInfo, action, exception);
                } catch (Exception var13) {
                    var13.printStackTrace();
                }
                break;
            case 2:
                try {
                    exception = (Exception)arg;
                    responseHandler.onSocketDisconnection(this.mConnectionInfo, action, exception);
                } catch (Exception var12) {
                    var12.printStackTrace();
                }
                break;
            case 3:
                try {
                    OriginalData data = (OriginalData)arg;
                    responseHandler.onSocketReadResponse(this.mConnectionInfo, action, data);
                } catch (Exception var11) {
                    var11.printStackTrace();
                }
                break;
            case 4:
            case 5:
                try {
                    responseHandler.onSocketIOThreadStart(action);
                } catch (Exception var10) {
                    var10.printStackTrace();
                }
                break;
            case 6:
                try {
                    ISendable sendable = (ISendable)arg;
                    responseHandler.onSocketWriteResponse(this.mConnectionInfo, action, sendable);
                } catch (Exception var9) {
                    var9.printStackTrace();
                }
                break;
            case 7:
            case 8:
                try {
                    exception = (Exception)arg;
                    responseHandler.onSocketIOThreadShutdown(action, exception);
                } catch (Exception var8) {
                    var8.printStackTrace();
                }
                break;
            case 9:
                try {
                    IPulseSendable sendable = (IPulseSendable)arg;
                    responseHandler.onPulseSend(this.mConnectionInfo, sendable);
                } catch (Exception var7) {
                    var7.printStackTrace();
                }
        }

    }

    public void sendBroadcast(String action, Serializable serializable) {
        OkSocketOptions option = this.mManager.getOption();
        if (option != null) {
            OkSocketOptions.ThreadModeToken token = option.getCallbackThreadModeToken();
            ActionBean bean;
            if (token != null) {
                bean = new ActionBean(action, serializable, this);
                ActionRunnable runnable = new ActionRunnable(bean);

                try {
                    token.handleCallbackEvent(runnable);
                } catch (Exception var10) {
                    var10.printStackTrace();
                }
            } else if (option.isCallbackInIndependentThread()) {
                bean = new ActionBean(action, serializable, this);
                ACTION_QUEUE.offer(bean);
            } else if (!option.isCallbackInIndependentThread()) {
                synchronized(this.mResponseHandlerList) {
                    List<ISocketActionListener> copyData = new ArrayList(this.mResponseHandlerList);
                    Iterator it = copyData.iterator();

                    while(it.hasNext()) {
                        ISocketActionListener listener = (ISocketActionListener)it.next();
                        this.dispatchActionToListener(action, serializable, listener);
                    }
                }
            } else {
                SLog.e("ActionDispatcher error action:" + action + " is not dispatch");
            }

        }
    }

    public void sendBroadcast(String action) {
        this.sendBroadcast(action, (Serializable)null);
    }

    public void setConnectionInfo(ConnectionInfo connectionInfo) {
        this.mConnectionInfo = connectionInfo;
    }

    static {
        HANDLE_THREAD.start();
    }

    public static class ActionRunnable implements Runnable {
        private ActionBean mActionBean;

        ActionRunnable(ActionBean actionBean) {
            this.mActionBean = actionBean;
        }

        public void run() {
            if (this.mActionBean != null && this.mActionBean.mDispatcher != null) {
                ActionDispatcher actionDispatcher = this.mActionBean.mDispatcher;
                synchronized(actionDispatcher.mResponseHandlerList) {
                    List<ISocketActionListener> copyData = new ArrayList(actionDispatcher.mResponseHandlerList);
                    Iterator it = copyData.iterator();

                    while(it.hasNext()) {
                        ISocketActionListener listener = (ISocketActionListener)it.next();
                        actionDispatcher.dispatchActionToListener(this.mActionBean.mAction, this.mActionBean.arg, listener);
                    }
                }
            }

        }
    }

    protected static class ActionBean {
        String mAction = "";
        Serializable arg;
        ActionDispatcher mDispatcher;

        public ActionBean(String action, Serializable arg, ActionDispatcher dispatcher) {
            this.mAction = action;
            this.arg = arg;
            this.mDispatcher = dispatcher;
        }
    }

    private static class DispatchThread extends AbsLoopThread {
        public DispatchThread() {
            super("client_action_dispatch_thread");
        }

        protected void runInLoopThread() throws Exception {
            ActionBean actionBean = (ActionBean)ActionDispatcher.ACTION_QUEUE.take();
            if (actionBean != null && actionBean.mDispatcher != null) {
                ActionDispatcher actionDispatcher = actionBean.mDispatcher;
                synchronized(actionDispatcher.mResponseHandlerList) {
                    List<ISocketActionListener> copyData = new ArrayList(actionDispatcher.mResponseHandlerList);
                    Iterator it = copyData.iterator();

                    while(it.hasNext()) {
                        ISocketActionListener listener = (ISocketActionListener)it.next();
                        actionDispatcher.dispatchActionToListener(actionBean.mAction, actionBean.arg, listener);
                    }
                }
            }

        }

        protected void loopFinish(Exception e) {
        }
    }
}

