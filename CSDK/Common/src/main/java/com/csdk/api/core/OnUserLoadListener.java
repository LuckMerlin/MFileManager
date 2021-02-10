package com.csdk.api.core;

import com.csdk.api.bean.User;

import java.util.List;

/**
 * Create LuckMerlin
 * Date 16:06 2020/10/29
 * TODO
 */
public interface OnUserLoadListener extends Listener {
    void onUserLoaded(int status, List<User> list);
}
