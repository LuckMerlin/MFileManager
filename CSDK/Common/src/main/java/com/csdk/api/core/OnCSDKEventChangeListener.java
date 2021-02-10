package com.csdk.api.core;

/**
 * Create LuckMerlin
 * Date 15:34 2020/12/2
 * TODO
 */
public interface OnCSDKEventChangeListener extends CSDKEvent{
    void onEventChanged(int event,Object arg);
}
