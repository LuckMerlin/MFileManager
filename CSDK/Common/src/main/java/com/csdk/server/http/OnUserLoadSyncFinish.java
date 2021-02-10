package com.csdk.server.http;

import com.csdk.api.bean.User;

import java.util.List;

/**
 * Create LuckMerlin
 * Date 10:44 2020/9/15
 * TODO
 */
public interface OnUserLoadSyncFinish {
    void onUserLoadSyncFinish(boolean succeed, int code, String note, List<User> data);
}
