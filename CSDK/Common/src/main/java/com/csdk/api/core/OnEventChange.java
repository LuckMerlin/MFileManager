package com.csdk.api.core;

/**
 * Create LuckMerlin
 * Date 11:32 2020/8/26
 * TODO
 */
public interface OnEventChange extends Event, Listener {
    void onEventChanged(int event, Object arg);
}
