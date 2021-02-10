package com.csdk.socket;

import java.net.Socket;

/**
 * Create LuckMerlin
 * Date 10:36 2020/12/22
 * TODO
 */
 abstract class OkSocketFactory {
    public OkSocketFactory() {
    }

    public abstract Socket createSocket(ConnectionInfo var1, OkSocketOptions var2) throws Exception;
}

