package com.csdk.socket;

/**
 * Create LuckMerlin
 * Date 10:35 2020/12/22
 * TODO
 */
public class OkSocket {
    private static ManagerHolder holder = ManagerHolder.getInstance();

    public OkSocket() {
    }

    public static IRegister<IServerActionListener, IServerManager> server(int serverPort) {
        return (IRegister)holder.getServer(serverPort);
    }

    public static IConnectionManager open(ConnectionInfo connectInfo) {
        return holder.getConnection(connectInfo);
    }

    public static IConnectionManager open(String ip, int port) {
        ConnectionInfo info = new ConnectionInfo(ip, port);
        return holder.getConnection(info);
    }

    /** @deprecated */
    public static IConnectionManager open(ConnectionInfo connectInfo, OkSocketOptions okOptions) {
        return holder.getConnection(connectInfo, okOptions);
    }

    /** @deprecated */
    public static IConnectionManager open(String ip, int port, OkSocketOptions okOptions) {
        ConnectionInfo info = new ConnectionInfo(ip, port);
        return holder.getConnection(info, okOptions);
    }
}
