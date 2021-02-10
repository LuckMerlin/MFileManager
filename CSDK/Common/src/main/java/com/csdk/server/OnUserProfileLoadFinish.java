package com.csdk.server;

import com.csdk.api.bean.User;

/**
 * Create LuckMerlin
 * Date 19:41 2020/11/26
 * TODO
 */
public interface OnUserProfileLoadFinish {
    void onUserProfileLoadFinish(boolean succeed, String note, User user);
}
