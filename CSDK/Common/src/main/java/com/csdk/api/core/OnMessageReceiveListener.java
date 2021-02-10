package com.csdk.api.core;

import com.csdk.api.bean.Message;

/**
 * Create LuckMerlin
 * Date 19:14 2020/10/29
 * TODO
 */
public interface OnMessageReceiveListener extends Listener {
    void onMessageReceived(Message message);
}
