package com.csdk.api.core;

import com.csdk.api.bean.User;

/**
 * Create LuckMerlin
 * Date 16:24 2020/9/14
 * TODO
 */
public interface OnLoginChange {
    void onLoginChange(User current,User last);
}
