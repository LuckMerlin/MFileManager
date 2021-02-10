package com.csdk.api.core;

/**
 * Create LuckMerlin
 * Date 15:01 2021/1/14
 * TODO
 */
public interface OnSendFinish {
    void onSendFinish(boolean succeed, String note, Object reply);
}
