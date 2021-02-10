package com.csdk.server.http;

/**
 * Create LuckMerlin
 * Date 10:44 2020/9/15
 * TODO
 */
public interface OnUserIdLoadSyncFinish {
    void onUserLoadSyncFinish(boolean succeed, int code, String note, String data);
}
