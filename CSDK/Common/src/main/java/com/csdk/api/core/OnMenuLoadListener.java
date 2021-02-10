package com.csdk.api.core;

import com.csdk.api.bean.Menu;
import com.csdk.api.bean.Group;

import java.util.List;

/**
 * Create LuckMerlin
 * Date 16:06 2020/10/29
 * TODO
 */
public interface OnMenuLoadListener extends Listener {
    void onMenuLoadFinish(int code, List<Menu<Group>> list);
}
