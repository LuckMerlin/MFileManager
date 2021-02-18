package com.csdk.api.common;

import android.widget.FrameLayout;

import com.csdk.api.audio.AudioManager;
import com.csdk.api.bean.CSDKAction;
import com.csdk.api.bean.Group;
import com.csdk.api.bean.Menu;
import com.csdk.api.bean.Message;
import com.csdk.api.bean.Session;
import com.csdk.api.audio.AudioPlayer;
import com.csdk.api.audio.AudioProcessor;
import com.csdk.api.bean.User;
import com.csdk.api.cache.Cache;
import com.csdk.api.config.Config;
import com.csdk.api.core.OnEventChange;
import com.csdk.api.core.OnSendFinish;
import com.csdk.api.core.Page;
import com.csdk.api.core.Role;
import com.csdk.api.core.Ui;
import com.csdk.server.Matchable;

import java.util.List;

/**
 * Create LuckMerlin
 * Date 19:39 2021/1/20
 * TODO
 */
public interface Api {
    int notifyActionChange(CSDKAction action, String args);
    int send(Message message, Session to, String charSet, OnSendFinish callback);
    int blockFriend(boolean block, Session session, OnSendFinish callback);
    boolean isSocketConnected();
    Role getLoginRole();
    boolean add(OnEventChange callback);
    boolean remove(OnEventChange callback);
    int setContentView(Object view, FrameLayout.LayoutParams params);
    long getSessionMessageBlockDuration(Session session);
    Page<Object, User> getFriends(int from,int size);
    Config getConfig();
    AudioPlayer getAudioPlayer();
    AudioManager getAudioManager();
    List<Menu<Group>> getMenus(Matchable matchable, int max);
    AudioProcessor getAudioProcessor();
    Cache getCache();
    Ui getUi();

}
