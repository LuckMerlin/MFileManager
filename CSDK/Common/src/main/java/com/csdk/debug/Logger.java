package com.csdk.debug;

import android.util.Log;

import com.csdk.server.Configure;
import com.hero.common.BuildConfig;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by luckmerlin on 2020/7/28.
 */

public final class Logger {
    private static boolean mPrint;

    private Logger(){
        Configure configure=Configure.getInstance();
        mPrint=null!=configure&&configure.isEncrptyLogEnable();
    }

    private final static String TAG = "imSdk";

    public static void D(String msg) {
        D(null, msg);
    }

    public static void D(String tag, String msg) {
        Log.d(null != tag && tag.length() > 0 ? (TAG + "." + tag) : TAG, null != msg ? msg : "");
    }

    public static void W(String msg) {
        W(null, msg);
    }

    public static void W(String tag, String msg) {
        Log.d(null != tag && tag.length() > 0 ? (TAG + "." + tag) : TAG, null != msg ? msg : "");
    }

    /**
     * Process print encrypt log which will disable while build as release
     */
    public static void M(String msg,String encrypt) {
        M(null, msg,encrypt);
    }

    /**
     * Process print encrypt log which will disable while build as release
     */
    public static void M(String tag, String msg,String encrypt) {
        boolean enableEncryptLog=mPrint|| BuildConfig.DEBUG;
        if (!enableEncryptLog&&null!=msg) {
            D(tag, msg);
        }
        if (enableEncryptLog) {
            Log.d(null != tag && tag.length() > 0 ? (TAG + "." + tag) : TAG, (null != encrypt ? encrypt : ""));
        }
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
        tag = null != tag && tag.length() > 0 ? (TAG + "." + tag) : TAG;
        msg = null != msg ? msg : "";
        Log.d(tag, msg);
        if (null != throwable) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            throwable.printStackTrace(printWriter);
            Log.e(tag, "Exception stack trace:\n" + stringWriter);
        }
    }
}
