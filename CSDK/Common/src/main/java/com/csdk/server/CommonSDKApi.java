package com.csdk.server;

import android.widget.FrameLayout;

import com.csdk.api.audio.AudioManager;
import com.csdk.api.audio.AudioPlayer;
import com.csdk.api.audio.AudioProcessor;
import com.csdk.api.bean.CSDKAction;
import com.csdk.api.bean.Group;
import com.csdk.api.bean.Menu;
import com.csdk.api.bean.Message;
import com.csdk.api.bean.Session;
import com.csdk.api.cache.Cache;
import com.csdk.api.common.Api;
import com.csdk.api.config.Config;
import com.csdk.api.core.Code;
import com.csdk.api.core.OnEventChange;
import com.csdk.api.core.OnMenuLoadListener;
import com.csdk.api.core.OnSendFinish;
import com.csdk.api.core.Role;
import com.csdk.api.core.Ui;
import com.csdk.server.socket.HeroSocket;

import java.util.List;

/**
 * Create LuckMerlin
 * Date 19:54 2021/1/22
 * TODO
 */
 abstract class CommonSDKApi implements Api {

    @Override
    public int notifyActionChange(CSDKAction action, String args) {
        HeroSocket socket=getHeroSocket();
        return null!=socket?socket.notifyActionChange(action, args, "While api call.")? Code.CODE_SUCCEED:Code.CODE_FAIL:Code.CODE_NONE_INITIAL;
    }

    @Override
    public int send(Message message, Session to, String charSet, OnSendFinish callback) {
        HeroSocket socket=getHeroSocket();
        if (null==socket){
            socket.notifySendFinish(false, "None initial", null, callback);
            return Code.CODE_NONE_INITIAL;
        }
        return socket.send(message, to,charSet, callback,"While api call.")?Code.CODE_SUCCEED:Code.CODE_FAIL;
    }

    @Override
    public boolean isSocketConnected() {
        HeroSocket socket=getHeroSocket();
        return null!=socket&&socket.isSocketConnected();
    }

    @Override
    public Role getLoginRole() {
        HeroSocket socket=getHeroSocket();
        return null!=socket?socket.getLoginRole():null;
    }

    @Override
    public long getSessionMessageBlockDuration(Session session) {
        return 0;
    }

    @Override
    public boolean add(OnEventChange callback) {
        HeroSocket socket=getHeroSocket();
        return null!=socket&&socket.add(callback, "While api call.");
    }

    @Override
    public boolean remove(OnEventChange callback) {
        HeroSocket socket=getHeroSocket();
        return null!=socket&&socket.remove(callback, "While api call.");
    }

    public abstract int setContentView(final Object contentViewObj, FrameLayout.LayoutParams params);

    @Override
    public Config getConfig() {
        Configure configure=Configure.getInstance();
        return null!=configure?configure.getConfig():null;
    }

    @Override
    public AudioPlayer getAudioPlayer() {
        return null;
    }

    @Override
    public AudioManager getAudioManager() {
        return null;
    }

    @Override
    public List<Menu<Group>> getMenus(Matchable matchable, int max) {
        HeroSocket socket=getHeroSocket();
        return null!=socket?socket.getChannels(matchable,max):null;
    }


    @Override
    public Ui getUi() {
        return new Ui() {
            @Override
            public int getProductMenus(OnMenuLoadListener callback) {
                HeroSocket socket=getHeroSocket();
                return null!=socket?socket.loadChannels((boolean succeed, int code, String note, List<Menu<Group>> list)-> {
                    if (null!=callback){
                        callback.onMenuLoadFinish(code, list);
                    }
                }, "While api call.")?Code.CODE_SUCCEED:Code.CODE_FAIL:Code.CODE_NONE_INITIAL;
            }
        };
    }

    @Override
    public AudioProcessor getAudioProcessor() {
        return null;
    }

    @Override
    public Cache getCache() {
        return null;
    }

    abstract HeroSocket getHeroSocket();

    @Override
    public int blockFriend(boolean block, Session session, OnSendFinish callback) {
        return 0;
    }
}
