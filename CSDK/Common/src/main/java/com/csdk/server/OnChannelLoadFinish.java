package com.csdk.server;


import com.csdk.api.bean.Group;
import com.csdk.api.bean.Menu;

import java.util.List;

/**
 * Create LuckMerlin
 * Date 11:42 2020/9/15
 * TODO
 */
public interface OnChannelLoadFinish {
    void onChannelLoadFinish(boolean succeed, int code, String note, List<Menu<Group>> list);
}
