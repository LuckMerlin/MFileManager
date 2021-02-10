package com.csdk.api.core;

import com.csdk.server.data.Frame;

/**
 * Create LuckMerlin
 * Date 10:13 2020/9/16
 * TODO
 * @deprecated
 */
public interface OnMessageReply {
    void onMessageReplied(boolean succeed, String note, Frame frame);
}
