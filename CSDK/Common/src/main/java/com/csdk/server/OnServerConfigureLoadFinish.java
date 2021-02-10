package com.csdk.server;

import com.csdk.data.ServerConfigure;

/**
 * Create LuckMerlin
 * Date 15:53 2021/1/5
 * TODO
 */
public interface OnServerConfigureLoadFinish {
    void onServerConfigureLoadFinish(boolean succeed, String note, ServerConfigure data);
}
