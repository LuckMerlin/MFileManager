package com.csdk.api.common;

import com.csdk.api.bean.CSDKAction;
import com.csdk.api.core.Listener;

/**
 * Create LuckMerlin
 * Date 18:45 2021/1/13
 * TODO
 */
public interface OnCSDKListener extends Listener {
    void onCsdkActionChange(CSDKAction action, String args);
}
