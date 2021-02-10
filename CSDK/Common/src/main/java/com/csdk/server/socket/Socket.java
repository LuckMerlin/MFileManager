package com.csdk.server.socket;

import com.csdk.debug.Logger;
import com.csdk.api.core.Status;
import com.csdk.socket.ConnectionInfo;
import com.csdk.socket.IConnectionManager;
import com.csdk.socket.IPulse;
import com.csdk.socket.IPulseSendable;
import com.csdk.socket.IReaderProtocol;
import com.csdk.socket.ISendable;
import com.csdk.socket.ISocketActionListener;
import com.csdk.socket.OkSocket;
import com.csdk.socket.OkSocketOptions;
import com.csdk.socket.OriginalData;
import com.csdk.socket.PulseManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2020/7/29.
 */

public class Socket  {
    private IConnectionManager mManager;
    private List<Runnable> mDisconnectCallback;
    private Connect mConnecting,mConnected;

    public interface OnSocketConnectionFinish{
        void onSocketConnectionFinish(boolean succeed, int status, ConnectionInfo connectionInfo, String s);
    }

    protected void onPulseSend(ConnectionInfo connectionInfo, IPulseSendable iPulseSendable) {
        //Do nothing
    }

    protected void onSocketConnectionFailed(ConnectionInfo ci, String s, Exception e) {
        Logger.M("Fail connect.","Fail connect socket."+(null!=ci?ci.getIp():"")+" "+(null!=ci?ci.getPort():-1));
    }

    protected void onSocketConnectionSuccess(ConnectionInfo connectionInfo, String s) {
       //Do nothing
    }

    protected void onSocketDisconnection(ConnectionInfo connectionInfo, String s, Exception e) {
        Logger.M("Disconnected.","Disconnected socket."+(null!=connectionInfo?connectionInfo.getIp():""));
    }

    protected void onSocketIOThreadShutdown(boolean self,String s, Exception e) {
        Logger.M("Shutdown server","Shutdown socket thread."+s+" "+e);
    }

    protected void onSocketIOThreadStart(String s) {
        Logger.M("Start server thread.","Start socket server thread."+s);
    }

    protected void onSocketReadResponse(ConnectionInfo connectionInfo, String s, OriginalData originalData) {
        //Do nothing
    }

    protected void onSocketWriteResponse(ConnectionInfo connectionInfo, String s, ISendable iSendable) {
        //Do nothing
    }

    protected void onConnecting(){
        //Do nothing
    }

    public final boolean setPulse(IPulseSendable pulseObj, boolean pulse, String debug){
        IConnectionManager connectionManager=null!=pulseObj?mManager:null;
        PulseManager pulseManager=null!=connectionManager?connectionManager.getPulseManager():null;
        if (null!=pulseManager){
            Logger.D("Set socket pulse "+debug);
            IPulse result= pulseManager.setPulseSendable(pulseObj);
            if (pulse&&null!=result){
                result.pulse();
                Logger.D("Pulse socket "+"after pulse set "+debug);
            }
            return true;
        }
        return false;
    }

    public final boolean feedPulse(){
        IConnectionManager manager=mManager;
        PulseManager pulseManager=null!=manager?manager.getPulseManager():null;
        if (null!=pulseManager) {
            pulseManager.feed();//Send pulse to server
            return true;
        }
        return false;
    }

    protected final synchronized boolean connect(final String ip, final int port, String serverId, String roleId, final OnSocketConnectionFinish
            connectionFinish, int heartPulse, IReaderProtocol readerProtocol, String debug){
        if (null==ip||port<=0){
            Logger.D("Can't connect server.Invalid address?"+debug+"ip="+ip+" port="+port);
            notifyConnectFinish(false, Status.STATUS_FAIL,null,null,connectionFinish);
            return false;
        }
        if (null==readerProtocol){
            Logger.D("Can't connect server while reader protocol NULL "+debug);
            notifyConnectFinish(false, Status.STATUS_FAIL,null,null,connectionFinish);
            return false;
        }
        IConnectionManager currManager=mManager;
        final Connect connecting=mConnecting=new Connect(ip, port,serverId,roleId);
        if (null!=currManager&&currManager.isConnect()){
            Connect connected=mConnected;
            if (null!=connected){
//                Check if already connected same server
                if (isStringEquals(connected.mIp,ip)&&port==connected.mPort&&isStringEquals(connected.
                        mServerId,serverId)&&isStringEquals(connected.mRoleId, roleId)){
                    cleanConnecting(connecting,"While Not need connect again while connected ");
                    Logger.D("Not need connect again while connected "+debug);
                    notifyConnectFinish(true, Status.STATUS_ALREADY_DONE,new ConnectionInfo(ip,port),null,connectionFinish);
                    return true;
                }
            }
            return disconnect(()->{
                Connect current=mConnecting;
                if (null!=current&&current==connecting){
                    connect(connecting.mIp,connecting.mPort,connecting.mServerId,roleId,
                            connectionFinish,heartPulse,readerProtocol,"After disconnect current client.");
                }
            },"While connect new client.");
        }
        final IConnectionManager manager= OkSocket.open(new ConnectionInfo(ip, port));
        if (null==manager){
            Logger.W("Fail connect socket while open socket fail "+debug);
            cleanConnecting(connecting,"While while open socket fail.");
            notifyConnectFinish(false, Status.STATUS_FAIL,null,null,connectionFinish);
            return false;
        }
        manager.registerReceiver(new ISocketActionListener(){
            @Override
            public void onPulseSend(ConnectionInfo connectionInfo, IPulseSendable iPulseSendable) {
                Socket.this.onPulseSend(connectionInfo, iPulseSendable);
            }

            @Override
            public void onSocketConnectionFailed(ConnectionInfo connectionInfo, String s, Exception e) {
                Socket.this.onSocketConnectionFailed(connectionInfo, s, e);
                notifyConnectFinish(false, Status.STATUS_FAIL,connectionInfo,s,connectionFinish);
            }

            @Override
            public void onSocketConnectionSuccess(ConnectionInfo connectionInfo, String s) {
                cleanConnecting(connecting,"Success connect socket.");
                mConnected=connecting;
                Logger.D("Success connect socket.");
                Socket.this.onSocketConnectionSuccess(connectionInfo, s);
                notifyConnectFinish(true, Status.STATUS_SUCCEED,connectionInfo,s,connectionFinish);
            }

            @Override
            public void onSocketDisconnection(ConnectionInfo connectionInfo, String s, Exception e) {
                cleanConnected(connecting,"Socket disconnect.");
                Socket.this.onSocketDisconnection(connectionInfo, s, e);
            }

            @Override
            public void onSocketIOThreadShutdown(String s, Exception e) {
                cleanConnecting(connecting,"Socket IO shutdown.");
                cleanConnected(connecting,"Socket IO shutdown.");
                IConnectionManager current=mManager;
                if (null!=current&&current==manager){
                    mManager=null;
                    manager.unRegisterReceiver(this);
                    Socket.this.onSocketIOThreadShutdown(true,s, e);
                    List<Runnable> callbacks=mDisconnectCallback;
                    if (null!=callbacks){
                        synchronized (callbacks){
                            for (Runnable callback:callbacks) {
                                if (null!=callback){
                                    callback.run();
                                }
                            }
                        }
                    }
                }else{
                    Socket.this.onSocketIOThreadShutdown(false,s, e);
                }
            }

            @Override
            public void onSocketIOThreadStart(String s) {
                Socket.this.onSocketIOThreadStart(s);
            }

            @Override
            public void onSocketReadResponse(ConnectionInfo connectionInfo, String s, OriginalData originalData) {
                Socket.this.onSocketReadResponse(connectionInfo, s, originalData);
            }

            @Override
            public void onSocketWriteResponse(ConnectionInfo connectionInfo, String s, ISendable iSendable) {
                Socket.this.onSocketWriteResponse(connectionInfo, s, iSendable);
            }
        });
        OkSocketOptions.Builder builder = new OkSocketOptions.Builder(manager.getOption());
        builder.setReaderProtocol(readerProtocol);
        builder.setPulseFrequency(heartPulse<=5000?10000:heartPulse);
        builder.setPulseFeedLoseTimes(3);
        builder.setConnectionHolden(false);
        manager.option(builder.build());
        manager.setIsConnectionHolder(false);
        Logger.M("Start connect server.","Start connect socket server "+debug+"ip="+ip+" port="+port);
        mDisconnectCallback=null;
        mManager=manager;
        onConnecting();
        manager.connect();
        return true;
    }

    public final boolean setKeepAlive(boolean keep,String debug){
        IConnectionManager manager=mManager;
        if (null!=manager) {
            manager.setIsConnectionHolder(keep);
            Logger.D("Set keep alive "+keep+" "+debug);
            return true;
        }
        return false;
    }

    public final boolean isOnline(){
        IConnectionManager manager=mManager;
        return null!=manager&&manager.isConnect();
    }

    public final boolean sendBytes(final byte[] bytes){
        if (null==bytes||bytes.length<=0){
            Logger.D("Can't send EMPTY bytes.");
            return false;
        }
        IConnectionManager manager=mManager;
        if (null!=manager){
            Logger.D("Send bytes "+(null!=bytes?bytes.length:-1));
            manager.send(()-> bytes);
            return true;
        }
        Logger.D("Can't send bytes,Not connected."+bytes);
        return false;
    }

    public final boolean isSocketConnected(){
        IConnectionManager manager=mManager;
        return null!=manager&&manager.isConnect();
    }

    private void notifyConnectFinish(boolean succeed,int status, ConnectionInfo connectionInfo, String s, OnSocketConnectionFinish callback){
        if (null!=callback){
            callback.onSocketConnectionFinish(succeed,status,connectionInfo,s);
        }
    }

    public final boolean isConnecting() {
        IConnectionManager manager = mManager;
        return null!=manager&&manager.isDisconnecting();
    }

    public  boolean disconnect(String debug){
        return disconnect(null, debug);
    }

    public  boolean disconnect(Runnable disconnectCallback,String debug){
        IConnectionManager manager=mManager;
        if (null!=manager&&manager.isConnect()){
            if (null!=disconnectCallback){
                List<Runnable> callbacks=mDisconnectCallback;
                callbacks=null!=callbacks?callbacks:(mDisconnectCallback=new ArrayList<>());
                synchronized (callbacks){
                    if (!callbacks.contains(disconnectCallback)){
                        callbacks.add(disconnectCallback);
                    }
                }
            }
            Logger.D("Disconnect server "+(null!=debug?debug:"."));
            manager.disconnect();
            return true;
        }
        return false;
    }

    public final String getIp() {
        IConnectionManager manager=mManager;
        ConnectionInfo info=null!=manager?manager.getConnectionInfo():null;
        return null!=info?info.getIp():null;
    }

    public final int getPort() {
        IConnectionManager manager=mManager;
        ConnectionInfo info=null!=manager?manager.getConnectionInfo():null;
        return null!=info?info.getPort():-1;
    }

    private boolean isStringEquals(String value1,String value2){
        return (null==value1&&null==value2)||(null!=value1&&null!=value2&&value1.equals(value2));
    }

    private boolean cleanConnecting(Connect connect,String debug){
        Connect currentConnecting=mConnecting;
        if (null!=connect&&null!=currentConnecting&&currentConnecting==connect){
            mConnecting=null;
            return true;
        }
        return false;
    }

    private boolean cleanConnected(Connect connect,String debug){
        Connect currentConnected=mConnected;
        if (null!=connect&&null!=currentConnected&&currentConnected==connect){
            mConnected=null;
            return true;
        }
        return false;
    }

    private static final class Connect{
        private final String mIp;
        private final int mPort;
        private final String mServerId;
        private final String mRoleId;

        Connect(String ip,int port,String serverId,String roleId){
            mServerId=serverId;
            mRoleId=roleId;
            mPort=port;
            mIp=ip;
        }
    }

}

