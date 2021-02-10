package com.csdk.socket;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * Create LuckMerlin
 * Date 10:26 2020/12/22
 * TODO
 */
class ConnectionManagerImpl  extends AbsConnectionManager {
    private volatile Socket mSocket;
    private volatile OkSocketOptions mOptions;
    private IIOManager mManager;
    private Thread mConnectThread;
    private ActionHandler mActionHandler;
    private volatile PulseManager mPulseManager;
    private volatile AbsReconnectionManager mReconnectionManager;
    private volatile boolean isConnectionPermitted = true;
    private volatile boolean isDisconnecting = false;

    protected ConnectionManagerImpl(ConnectionInfo info) {
        super(info);
        String ip = "";
        String port = "";
        if (info != null) {
            ip = info.getIp();
            port = info.getPort() + "";
        }

        SLog.i("block connection init with:" + ip + ":" + port);
    }

    public synchronized void connect() {
        SLog.i("Thread name:" + Thread.currentThread().getName() + " id:" + Thread.currentThread().getId());
        if (this.isConnectionPermitted) {
            this.isConnectionPermitted = false;
            if (!this.isConnect()) {
                this.isDisconnecting = false;
                if (this.mConnectionInfo == null) {
                    this.isConnectionPermitted = true;
                    throw new UnConnectException("连接参数为空,检查连接参数");
                } else {
                    if (this.mActionHandler != null) {
                        this.mActionHandler.detach(this);
                        SLog.i("mActionHandler is detached.");
                    }

                    this.mActionHandler = new ActionHandler();
                    this.mActionHandler.attach(this, this);
                    SLog.i("mActionHandler is attached.");
                    if (this.mReconnectionManager != null) {
                        this.mReconnectionManager.detach();
                        SLog.i("ReconnectionManager is detached.");
                    }

                    this.mReconnectionManager = this.mOptions.getReconnectionManager();
                    if (this.mReconnectionManager != null) {
                        this.mReconnectionManager.attach(this);
                        SLog.i("ReconnectionManager is attached.");
                    }

                    try {
                        this.mSocket = this.getSocketByConfig();
                    } catch (Exception var2) {
                        if (this.mOptions.isDebug()) {
                            var2.printStackTrace();
                        }

                        this.isConnectionPermitted = true;
                        throw new UnConnectException("创建Socket失败.", var2);
                    }

                    String info = this.mConnectionInfo.getIp() + ":" + this.mConnectionInfo.getPort();
                    this.mConnectThread = new ConnectionThread(" Connect thread for " + info);
                    this.mConnectThread.setDaemon(true);
                    this.mConnectThread.start();
                }
            }
        }
    }

    private Socket getSocketByConfig() throws Exception {
        if (this.mOptions.getOkSocketFactory() != null) {
            return this.mOptions.getOkSocketFactory().createSocket(this.mConnectionInfo, this.mOptions);
        } else {
            OkSocketSSLConfig config = this.mOptions.getSSLConfig();
            if (config == null) {
                return new Socket();
            } else {
                SSLSocketFactory factory = config.getCustomSSLFactory();
                if (factory == null) {
                    String protocol = "SSL";
                    if (!TextUtils.isEmpty(config.getProtocol())) {
                        protocol = config.getProtocol();
                    }

                    TrustManager[] trustManagers = config.getTrustManagers();
                    if (trustManagers == null || trustManagers.length == 0) {
                        trustManagers = new TrustManager[]{new DefaultX509ProtocolTrustManager()};
                    }

                    try {
                        SSLContext sslContext = SSLContext.getInstance(protocol);
                        sslContext.init(config.getKeyManagers(), trustManagers, new SecureRandom());
                        return sslContext.getSocketFactory().createSocket();
                    } catch (Exception var7) {
                        if (this.mOptions.isDebug()) {
                            var7.printStackTrace();
                        }

                        SLog.e(var7.getMessage());
                        return new Socket();
                    }
                } else {
                    try {
                        return factory.createSocket();
                    } catch (IOException var6) {
                        if (this.mOptions.isDebug()) {
                            var6.printStackTrace();
                        }

                        SLog.e(var6.getMessage());
                        return new Socket();
                    }
                }
            }
        }
    }

    private void resolveManager() throws IOException {
        this.mPulseManager = new PulseManager(this, this.mOptions);
        this.mManager = new IOThreadManager(this.mSocket.getInputStream(), this.mSocket.getOutputStream(), this.mOptions, this.mActionDispatcher);
        this.mManager.startEngine();
    }

    public void disconnect(Exception exception) {
        synchronized(this) {
            if (this.isDisconnecting) {
                return;
            }

            this.isDisconnecting = true;
            if (this.mPulseManager != null) {
                this.mPulseManager.dead();
                this.mPulseManager = null;
            }
        }

        if (exception instanceof ManuallyDisconnectException && this.mReconnectionManager != null) {
            this.mReconnectionManager.detach();
            SLog.i("ReconnectionManager is detached.");
        }

        synchronized(this) {
            String info = this.mConnectionInfo.getIp() + ":" + this.mConnectionInfo.getPort();
            DisconnectThread thread = new DisconnectThread(exception, "Disconnect Thread for " + info);
            thread.setDaemon(true);
            thread.start();
        }
    }

    public void disconnect() {
        this.disconnect(new ManuallyDisconnectException());
    }

    public IConnectionManager send(ISendable sendable) {
        if (this.mManager != null && sendable != null && this.isConnect()) {
            this.mManager.send(sendable);
        }

        return this;
    }

    public IConnectionManager option(OkSocketOptions okOptions) {
        if (okOptions == null) {
            return this;
        } else {
            this.mOptions = okOptions;
            if (this.mManager != null) {
                this.mManager.setOkOptions(this.mOptions);
            }

            if (this.mPulseManager != null) {
                this.mPulseManager.setOkOptions(this.mOptions);
            }

            if (this.mReconnectionManager != null && !this.mReconnectionManager.equals(this.mOptions.getReconnectionManager())) {
                if (this.mReconnectionManager != null) {
                    this.mReconnectionManager.detach();
                }

                SLog.i("reconnection manager is replaced");
                this.mReconnectionManager = this.mOptions.getReconnectionManager();
                this.mReconnectionManager.attach(this);
            }

            return this;
        }
    }

    public OkSocketOptions getOption() {
        return this.mOptions;
    }

    public boolean isConnect() {
        if (this.mSocket == null) {
            return false;
        } else {
            return this.mSocket.isConnected() && !this.mSocket.isClosed();
        }
    }

    public boolean isDisconnecting() {
        return this.isDisconnecting;
    }

    public PulseManager getPulseManager() {
        return this.mPulseManager;
    }

    public void setIsConnectionHolder(boolean isHold) {
        this.mOptions = (new OkSocketOptions.Builder(this.mOptions)).setConnectionHolden(isHold).build();
    }

    public AbsReconnectionManager getReconnectionManager() {
        return this.mOptions.getReconnectionManager();
    }

    private class DisconnectThread extends Thread {
        private Exception mException;

        public DisconnectThread(Exception exception, String name) {
            super(name);
            this.mException = exception;
        }

        public void run() {
            try {
                if (ConnectionManagerImpl.this.mManager != null) {
                    ConnectionManagerImpl.this.mManager.close(this.mException);
                }

                if (ConnectionManagerImpl.this.mConnectThread != null && ConnectionManagerImpl.this.mConnectThread.isAlive()) {
                    ConnectionManagerImpl.this.mConnectThread.interrupt();

                    try {
                        SLog.i("disconnect thread need waiting for connection thread done.");
                        ConnectionManagerImpl.this.mConnectThread.join();
                    } catch (InterruptedException var7) {
                    }

                    SLog.i("connection thread is done. disconnection thread going on");
                    ConnectionManagerImpl.this.mConnectThread = null;
                }

                if (ConnectionManagerImpl.this.mSocket != null) {
                    try {
                        ConnectionManagerImpl.this.mSocket.close();
                    } catch (IOException var6) {
                    }
                }

                if (ConnectionManagerImpl.this.mActionHandler != null) {
                    ConnectionManagerImpl.this.mActionHandler.detach(ConnectionManagerImpl.this);
                    SLog.i("mActionHandler is detached.");
                    ConnectionManagerImpl.this.mActionHandler = null;
                }
            } finally {
                ConnectionManagerImpl.this.isDisconnecting = false;
                ConnectionManagerImpl.this.isConnectionPermitted = true;
                if (!(this.mException instanceof UnConnectException) && ConnectionManagerImpl.this.mSocket != null) {
                    this.mException = this.mException instanceof ManuallyDisconnectException ? null : this.mException;
                    ConnectionManagerImpl.this.sendBroadcast("action_disconnection", this.mException);
                }

                ConnectionManagerImpl.this.mSocket = null;
                if (this.mException != null) {
                    SLog.e("socket is disconnecting because: " + this.mException.getMessage());
                    if (ConnectionManagerImpl.this.mOptions.isDebug()) {
                        this.mException.printStackTrace();
                    }
                }

            }

        }
    }

    private class ConnectionThread extends Thread {
        public ConnectionThread(String name) {
            super(name);
        }

        public void run() {
            try {
                SLog.i("Start connect: " + ConnectionManagerImpl.this.mConnectionInfo.getIp() + ":" + ConnectionManagerImpl.this.mConnectionInfo.getPort() + " socket server...");
                ConnectionManagerImpl.this.mSocket.connect(new InetSocketAddress(ConnectionManagerImpl.this.mConnectionInfo.getIp(), ConnectionManagerImpl.this.mConnectionInfo.getPort()), ConnectionManagerImpl.this.mOptions.getConnectTimeoutSecond() * 1000);
                ConnectionManagerImpl.this.mSocket.setTcpNoDelay(true);
                ConnectionManagerImpl.this.resolveManager();
                ConnectionManagerImpl.this.sendBroadcast("action_connection_success");
                SLog.i("Socket server: " + ConnectionManagerImpl.this.mConnectionInfo.getIp() + ":" + ConnectionManagerImpl.this.mConnectionInfo.getPort() + " connect successful!");
            } catch (Exception var6) {
                if (ConnectionManagerImpl.this.mOptions.isDebug()) {
                    var6.printStackTrace();
                }

                Exception exception = new UnConnectException(var6);
                SLog.e("Socket server " + ConnectionManagerImpl.this.mConnectionInfo.getIp() + ":" + ConnectionManagerImpl.this.mConnectionInfo.getPort() + " connect failed! error msg:" + var6.getMessage());
                ConnectionManagerImpl.this.sendBroadcast("action_connection_failed", exception);
            } finally {
                ConnectionManagerImpl.this.isConnectionPermitted = true;
            }

        }
    }
}
