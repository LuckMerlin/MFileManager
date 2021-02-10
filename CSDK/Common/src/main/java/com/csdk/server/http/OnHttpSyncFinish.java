package com.csdk.server.http;

import okhttp3.Call;

/**
 * Create LuckMerlin
 * Date 12:10 2020/11/11
 * TODO
 */
public interface OnHttpSyncFinish<T> {
    void onSyncFinish(boolean succeed, Call call, String note, T data);
}
