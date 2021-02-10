package com.csdk.api.core;

import com.csdk.api.bean.CSDKAction;

/**
 * Create LuckMerlin
 * Date 16:54 2020/10/15
 * TODO
 */
public interface OnActionChangeListener extends  OnFriendshipActionListener,OnCSDKEventChangeListener{
    void onCsdkActionChange(CSDKAction action, String args);
}
