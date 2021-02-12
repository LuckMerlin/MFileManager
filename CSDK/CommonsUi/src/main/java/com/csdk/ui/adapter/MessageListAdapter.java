package com.csdk.ui.adapter;

import com.csdk.api.bean.Message;

public class MessageListAdapter extends ListAdapter<Message> {

    public boolean replaceMessageWithResendStatusCheck(Message message, String debug) {
        return false;
    }
}
