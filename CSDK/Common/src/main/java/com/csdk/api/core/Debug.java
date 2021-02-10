package com.csdk.api.core;


import com.csdk.debug.Logger;

/**
 * Created by luckmerlin on 2020/7/28.
 */

public final class Debug {

    private Debug(){
    }

    public static void D(String msg) {
        Logger.D(msg);
    }

    public static void D(String tag, String msg) {
        Logger.D(tag,msg);
    }

    public static void W(String msg) {
        Logger.W(null, msg);
    }

    public static void M(String msg,String encrypt) {
        M(null, msg,encrypt);
    }

    public static void M(String tag, String msg,String encrypt) {
        Logger.M(tag,msg,encrypt);
    }

    public static void E(String msg) {
        E(null, msg);
    }

    public static void E(String msg, Throwable throwable) {
        E(null, msg, throwable);
    }

    public static void E(String tag, String msg) {
        E(tag, msg, null);
    }

    public static void E(String tag, String msg, Throwable throwable) {
        Logger.E(tag,msg,throwable);
    }
}
