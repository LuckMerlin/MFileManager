package com.csdk.api.core;

import com.csdk.api.bean.Reply;

public interface OnRequestReply {
    void onRequestReply(boolean succeed, String note, Reply reply);
}
