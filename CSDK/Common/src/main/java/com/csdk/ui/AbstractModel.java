package com.csdk.ui;

import com.csdk.api.audio.AudioManager;
import com.csdk.api.bean.CSDKAction;
import com.csdk.api.bean.Group;
import com.csdk.api.bean.Menu;
import com.csdk.api.bean.Message;
import com.csdk.api.bean.Session;
import com.csdk.api.bean.User;
import com.csdk.api.cache.Cache;
import com.csdk.api.common.Api;
import com.csdk.api.audio.AudioPlayer;
import com.csdk.api.audio.AudioProcessor;
import com.csdk.api.config.Config;
import com.csdk.api.core.Code;
import com.csdk.api.core.ContentType;
import com.csdk.api.core.Debug;
import com.csdk.api.core.OnSendFinish;
import com.csdk.api.core.Operation;
import com.csdk.api.core.Page;
import com.csdk.api.core.Role;
import com.csdk.debug.Logger;
import com.csdk.server.Matchable;

import java.util.List;

/**
 * Create LuckMerlin
 * Date 17:11 2020/8/21
 * TODO
 */
public class AbstractModel {
    private final Api mApi;

    public AbstractModel(Api api){
        mApi=api;
    }

    /**
     * @deprecated
     */
    protected final AudioPlayer getAudioPlayer() {
        Api api=mApi;
        return null!=api?api.getAudioPlayer():null;
    }

    protected final AudioManager getAudioManager() {
        Api api=mApi;
        return null!=api?api.getAudioManager():null;
    }

    protected final long getSessionMessageBlockDuration(Session session){
        Api api=mApi;
        return null!=api?api.getSessionMessageBlockDuration(session):-1;
    }

    protected final AudioProcessor getAudioProcessor() {
        Api api=mApi;
        return null!=api?api.getAudioProcessor():null;
    }

    protected final List<Menu<Group>> getMenus(Matchable matchable, int max) {
        Api api=mApi;
        return null!=api?api.getMenus(matchable,max):null;
    }

    protected final Session createSessionFromMessage(Message message){
        if (null!=message){
            String groupType=message.getGroupType();
            String groupId=message.getGroupId();
            if (null!=groupType&&null!=groupId&&groupType.length()>0&&groupId.length()>0){
                return new Group(groupId, groupType);
            }
            String toUid=message.getFirstToUid();
            if (null!=toUid&&toUid.length()>0){
                return new User(toUid);
            }
        }
        return null;
    }


    protected final boolean blockFriend(boolean block, Session session, OnSendFinish callback){
        Api api=getApi();
        if (null==api){
            return notifySendFinish(false, null, null, callback)&&false;
        }
        return api.blockFriend(block, session, callback)==Code.CODE_SUCCEED;
    }

    protected final Group getFirstMatchedGroup(String type){
        Menu<Group> menu=getFirstMatchedMenu(type);
        return null!=menu?menu.getGroup():null;
    }

    protected final Menu getFirstMatchedMenu(String type){
        Api api=getApi();
        List<Menu<Group>> groups=null!=api?api.getMenus((Object arg)-> {
                Group group=null!=arg&&arg instanceof Menu?((Menu)arg).getGroup():null;
                String groupType=null!=group?group.getType():null;
                return null!=groupType&&(null==type||groupType.equals(type))?Matchable.MATCHED:Matchable.CONTINUE;
        }, 1):null;
       return null!=groups&&groups.size()>0?groups.get(0):null;
    }

    protected final Config getConfig() {
        Api api=mApi;
        return null!=api?api.getConfig():null;
    }

    protected final boolean sendTextMessage(String text,Session to,OnSendFinish callback,String debug){
        return send(text,to, ContentType.CONTENTTYPE_TEXT,callback,debug);
    }

    protected final boolean send(String text,Session to,String contentType,OnSendFinish callback,String debug){
        if (null==to){
            Debug.W("Can't not send message while session invalid.");
            return notifySendFinish(false, "Session invalid.", null, callback)&&false;
        }
        if (null==text||text.length()<=0){
            Debug.W("Can't not send message while text invalid.");
            return notifySendFinish(false, "Text invalid.", null, callback)&&false;
        }
        if (null==contentType||contentType.length()<=0){
            Debug.W("Can't not send message while content type invalid.");
            return notifySendFinish(false, "Content type invalid.", null, callback)&&false;
        }
        Api api=getApi();
        if (null==api){
            Debug.W("Can't not send message while api NULL.");
            return notifySendFinish(false, "Api null.", null, callback)&&false;
        }
        Message message = new Message(Operation.SEND,null, text,contentType, null, null);
        Debug.M(null, "Send message."+text);
        return api.send(message, to, null, null)== Code.CODE_SUCCEED;
    }

    protected final boolean send(Message message,Session session,OnSendFinish callback,String debug){
        Api api=getApi();
        if (null==api){
            Debug.W("Can't not send message while api NULL.");
            return notifySendFinish(false, "Api null.", null, callback)&&false;
        }
        return api.send(message, session, null, null)== Code.CODE_SUCCEED;
    }

    protected final int notifyActionChange(CSDKAction action, String args, String debug){
        Api api=getApi();
        if (null==api){
            Logger.W("Can't notify action change while NONE initial "+(null!=debug?debug:"."));
            return Code.CODE_NONE_INITIAL;
        }else if (!isLogin()){
            Logger.W("Can't notify action change while NONE login "+(null!=debug?debug:"."));
            return Code.CODE_NONE_LOGIN;
        }
        return api.notifyActionChange(action,args);
    }

    protected final boolean isLogin(Object ...sessions){
        Role role=getLoginRole();
        if (null==sessions||sessions.length<=0){
            return null!=role;
        }
        return false;
    }

    protected final User getCachedUser(Object object){
        List<User> users=getCachedUser((Object arg) -> Matchable.BREAK,1);
        return null!=users&&users.size()>0?users.get(0):null;
    }

    protected final List<User> getCachedUser(Matchable matchable,int max){
        Api api=mApi;
        Cache cache=null!=api?api.getCache():null;
        return null!=cache?cache.getCachedUser(matchable, max):null;
    }

    protected final Page<Object,Message> getCachedMessage(Session session, Message from, int size, boolean setRead){
        Api api=mApi;
        Cache cache=null!=api?api.getCache():null;
        return null!=cache?cache.getCachedMessage(session,from,size,setRead):null;
    }

    protected final boolean notifySendFinish(boolean succeed, String note, Object reply,OnSendFinish callback){
        if (null!=callback){
            callback.onSendFinish(succeed,note,reply);
            return true;
        }
        return false;
    }

    protected final String getLoginRoleId(){
        Role role=getLoginRole();
        return null!=role?role.getRoleId():null;
    }

    protected final String getLoginUid() {
        Role role=getLoginRole();
        return null!=role?role.getUserId():null;
    }

    protected final Role getLoginRole() {
        Api api=mApi;
        return null!=api?api.getLoginRole():null;
    }

    protected final boolean isSocketConnected() {
        Api api=mApi;
        return null!=api&&api.isSocketConnected();
    }

    protected final Api getApi() {
        return mApi;
    }


}
