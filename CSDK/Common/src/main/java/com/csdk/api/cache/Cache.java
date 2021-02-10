package com.csdk.api.cache;

import com.csdk.api.bean.Message;
import com.csdk.api.bean.Session;
import com.csdk.api.bean.User;
import com.csdk.api.core.Page;
import com.csdk.server.Matchable;
import java.util.List;

/**
 * Create LuckMerlin
 * Date 19:29 2021/1/22
 * TODO
 */
public interface Cache {
    Page<Object,Message> getCachedMessage(Session session, Message from, int size, boolean setRead);
    List<User> getCachedUser(Matchable matchable,int max);

}
