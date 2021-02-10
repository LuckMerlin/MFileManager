package com.csdk.server.cache;

import com.csdk.api.bean.AddFriendRequest;
import com.csdk.api.bean.Group;
import com.csdk.api.bean.Message;
import com.csdk.api.bean.Session;
import com.csdk.api.bean.User;
import com.csdk.api.core.ContentType;
import com.csdk.api.core.Event;
import com.csdk.api.core.MessageType;
import com.csdk.debug.Logger;
import com.csdk.api.core.Status;
import com.csdk.data.OnMessageLoadFinish;
import com.csdk.server.util.ThreadPoolUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

class CSDKCache {
    private final CacheTool mCacheTool;
    private final HashMap<String,ArrayList<String>> mRecentChatMap=new HashMap<>();
    private final HashMap<String,HashMap<String,ArrayList<Message>>> mUnReadMessageMap=new HashMap<>();
    private final HashMap<String,ArrayList<AddFriendRequest>> mAddFriendRequestMap=new HashMap<>();
    private final HashMap<String,HashMap<String,ArrayList<Message>>> mGroupMessageMap=new HashMap<>();
    private final HashMap<String,HashMap<String,ArrayList<Message>>> mFriendMessageMap=new HashMap<>();
    private static final String LABEL_RECENT_CHAT="recentChatUser";
    private static final String LABEL_UNREAD_MESSAGE="unReadMessages";
    private static final String LABEL_ADD_FRIEND_REQUEST="addFriendRequest";
    private static final String LABEL_GROUP_MESSAGE="groupMessage";
    private static final String LABEL_FRIEND_MESSAGE="friendMessages";

    public CSDKCache(String cachePath){
        CacheTool cacheTool=mCacheTool=(null!=cachePath&&cachePath.length()>0?CacheTool.get(new File(cachePath)):null);
        try {
            HashMap<String,ArrayList<String>>  users=(HashMap<String,ArrayList<String>>)cacheTool.getAsObject(LABEL_RECENT_CHAT);
            if (null!=users&&users.size()>0){
                mRecentChatMap.putAll(users);
            }
        } catch (Exception e) {
            //Do nothing
        }
        try {
            HashMap<String,HashMap<String,ArrayList<Message>>>  messages=(HashMap<String,HashMap<String,ArrayList<Message>>>)
                    cacheTool.getAsObject(LABEL_UNREAD_MESSAGE);
            if (null!=messages&&messages.size()>0){
                mUnReadMessageMap.putAll(messages);
            }
        } catch (Exception e) {
            //Do nothing
        }

        try {
            HashMap<String,ArrayList<AddFriendRequest>>  requests=(HashMap<String,ArrayList<AddFriendRequest>>)
                    cacheTool.getAsObject(LABEL_ADD_FRIEND_REQUEST);
            if (null!=requests&&requests.size()>0){
                mAddFriendRequestMap.putAll(requests);
            }
        } catch (Exception e) {
            //Do nothing
        }

//        try {
//            HashMap<String,HashMap<String,ArrayList<Message>>>  messages=(HashMap<String,HashMap<String,ArrayList<Message>>>)
//                    cacheTool.getAsObject(LABEL_GROUP_MESSAGE);
//            if (null!=messages&&messages.size()>0){
//                mGroupMessageMap.putAll(messages);
//            }
//        } catch (Exception e) {
//            //Do nothing
//        }

        try {
            HashMap<String,HashMap<String,ArrayList<Message>>>  messages=(HashMap<String,HashMap<String,ArrayList<Message>>>)
                    cacheTool.getAsObject(LABEL_FRIEND_MESSAGE);
            if (null!=messages&&messages.size()>0){
                mFriendMessageMap.putAll(messages);
            }
        } catch (Exception e) {
            //Do nothing
        }
    }

    protected void notifyEvent(int event,Object arg){
        //Do nothing
    }

    public final List<Message> getUnreadMessages(String loginUid, Session session, int from, int size){
        if (null==loginUid||loginUid.length()<=0){
            Logger.W("Can't get unread message while login uid or session invalid.");
            return null;
        }
        HashMap<String,HashMap<String,ArrayList<Message>>> unReadMessageMap=mUnReadMessageMap;
        HashMap<String,ArrayList<Message>> loginMap=null!=unReadMessageMap?unReadMessageMap.get(loginUid):null;
        if (null==loginMap||loginMap.size()<=0){
            return null;//None any message
        }
        if(null==session){//Fetch all unread friend message
            final ArrayList<Message> allUnReadMessages=new ArrayList<>();
            synchronized (loginMap){
                Set<String> set=loginMap.keySet();
                if (null!=set&&set.size()>0){
                    for (String child:set) {
                        ArrayList<Message> messages=null!=child?loginMap.get(child):null;
                        if (null!=messages&&messages.size()>0){
                            allUnReadMessages.addAll(messages);
                        }
                    }
                }
            }
            return allUnReadMessages;
        }
        final String sessionUnique=createSessionUnique(session);
        return null!=sessionUnique&&sessionUnique.length()>0?loginMap.get(sessionUnique):null;
    }

    public final boolean setMessagesAllRead(String loginUid, Session session,String debug){
        if (null==loginUid||loginUid.length()<=0||null==session){
            Logger.W("Can't set message all read while login uid or session invalid.");
            return false;
        }
        HashMap<String,HashMap<String,ArrayList<Message>>> unReadMessageMap=mUnReadMessageMap;
        HashMap<String,ArrayList<Message>> loginMap=null!=unReadMessageMap?unReadMessageMap.get(loginUid):null;
        if (null==loginMap||loginMap.size()<=0){//Not need set read while none exist
            return false;
        }
        final String sessionUnique=null!=session?createSessionUnique(session):null;
        if (null==sessionUnique||sessionUnique.length()<=0){
            Logger.W("Can't set message all read while session unique create invalid.");
            return false;
        }
        if (loginMap.containsKey(sessionUnique)){
            loginMap.remove(sessionUnique);
            save(Event.EVENT_FRIEND_CHAT_SESSION_UNREAD_MESSAGE_LIST_CHANGE,session,"After set message all read.");
            return true;
        }
        return false;
    }

    public final boolean getCachedMessage(String loginUid,Session session,Object from, int size, OnMessageLoadFinish callback){
        HashMap<String,HashMap<String,ArrayList<Message>>> messageMap=mFriendMessageMap;
        if (null!=messageMap){

        }
        return false;
    }

    public final boolean deleteCachedMessage(Message message,String debug){
        String loginUid=null!=message?message.getLoginUid():null;
        if (null==loginUid||loginUid.length()<=0){
            Logger.W("Fail delete cache message while login uid invalid "+(null!=debug?debug:"."));
            return false;
        }
        String messageUnique=null!=message?getMessageSessionUnique(message):null;
        if (null==messageUnique||messageUnique.length()<=0){
            Logger.W("Fail delete cache message while unique value invalid "+(null!=debug?debug:"."));
            return false;
        }
        switch (message.getMsgType()){
            case MessageType.MESSAGETYPE_GROUP:
                HashMap<String,HashMap<String,ArrayList<Message>>> groupMap=mGroupMessageMap;
                if (null!=groupMap){
                    HashMap<String,ArrayList<Message>> groupMessages=null;
                    synchronized (groupMap){
                        groupMessages=groupMap.get(loginUid);
                    }
                    List<Message> messages=null!=groupMessages?groupMessages.get(messageUnique):null;
                    if (null!=messages){
                        synchronized (messages){
                            if (messages.remove(message)){
                                return save(Event.EVENT_MESSAGE_DELETE, message,debug)||true;
                            }
                        }
                    }
                }
                break;
            case MessageType.MESSAGETYPE_SINGLE:
                HashMap<String,HashMap<String,ArrayList<Message>>> friendMap=mFriendMessageMap;
                if (null!=friendMap){
                    HashMap<String,ArrayList<Message>> friendMessages=null;
                    synchronized (friendMap){
                        friendMessages=friendMap.get(loginUid);
                    }
                    List<Message> messages=null!=friendMessages?friendMessages.get(messageUnique):null;
                    if (null!=messages){
                        synchronized (messages){
                            if (messages.remove(message)){
                                return save(Event.EVENT_MESSAGE_DELETE, message,debug)||true;
                            }
                        }
                    }
                }
                break;
        }
        return false;
    }

    public final boolean cache(Cacheable cacheable, String debug){
        if (null==cacheable){
            return false;
        }else if (cacheable instanceof Message){
            return cacheMessage((Message)cacheable,debug);
        }else if (cacheable instanceof AddFriendRequest){
            return cacheAddRequest((AddFriendRequest)cacheable,debug);
        }
        return false;
    }

    /**
     * @deprecated
     */
    public Message testSaveMessageReceoptMessage(String loginUid,Message message){
        if (null!=loginUid&&null!=message&&message.getMsgType()==MessageType.MESSAGETYPE_SINGLE){
            String toUid=message.getFirstToUid();
            if (null!=toUid&&message.isContentType(ContentType.CONTENTTYPE_TEXT)){
                HashMap<String,HashMap<String,ArrayList<Message>>> friendMessageMap=mFriendMessageMap;
                HashMap<String,ArrayList<Message>> maps=null!=friendMessageMap?friendMessageMap.get(loginUid):null;
                final String singleSessionUnique=createSessionUnique(new User(toUid));
                ArrayList<Message> messages=null!=maps?maps.get(singleSessionUnique):null;
                int index=null!=messages?messages.indexOf(message):-1;
                Message current=index>=0?messages.get(index):null;
                if (null!=current){
                    current.setContent(message.getContent());
                    save(null, null, "While test save message receipt.");
                    return current;
                }
            }
        }
        return null;
    }

    private String getMessageSessionUnique(Message message){
        if (null!=message){
            switch (message.getMsgType()){
                case MessageType.MESSAGETYPE_SINGLE:
                    final String loginUid = null!=message?message.getLoginUid():null;
                    final String fromUid = message.getFromUid();
                    final boolean isSelf=null == fromUid || fromUid.equals(loginUid);
                    final String toUid=message.getFirstToUid();
                    String sessionToUid=isSelf ?toUid: fromUid;
                    return null!=sessionToUid&&sessionToUid.length()>0?createSessionUnique(new User(sessionToUid)):null;
                case MessageType.MESSAGETYPE_GROUP:
                    String groupType=message.getGroupType();
                    String groupId=message.getGroupId();
                    return null!=groupType&&null!=groupId&&groupType.length()>0&&groupId.
                            length()>0?createSessionUnique(new Group(groupId,groupType)):null;
            }
        }
        return null;
    }

    private boolean cacheMessage(Message message,String debug){
        final String loginUid = null!=message?message.getLoginUid():null;
        if (null == loginUid || loginUid.length() <= 0) {
            Logger.W("Can't cache message while receive id INVALID " + (null != debug ? debug : "."));
            return false;
        }
        final String fromUid = message.getFromUid();
        final boolean isSelf=null == fromUid || fromUid.equals(loginUid);
        final String toUid=message.getFirstToUid();
        switch (message.getMsgType()) {
            case MessageType.MESSAGETYPE_SINGLE:
                //Cache into recent chat list
                if (null!=toUid&&toUid.length()>0&&null!=loginUid&&loginUid.length()>0&&!loginUid.equals(toUid)){
                    cacheFriendRecentContact(loginUid,toUid);
                }
                String sessionToUid=isSelf ?toUid: fromUid;
                final String singleSessionUnique=null!=sessionToUid&&sessionToUid.length()>0?createSessionUnique(new User(sessionToUid)):null;
                if (!isSelf){//Make received message default as unread
                    //Cache into friend unread message
                    if (null!=singleSessionUnique&&singleSessionUnique.length()>0) {//Only valid session unique can save
                        HashMap<String,HashMap<String,ArrayList<Message>>> unreadMessageMap=mUnReadMessageMap;
                        HashMap<String,ArrayList<Message>> sessionUnreadMessageMap=unreadMessageMap.get(loginUid);
                        sessionUnreadMessageMap=null!=sessionUnreadMessageMap?sessionUnreadMessageMap:new HashMap<>();
                        unreadMessageMap.put(loginUid, sessionUnreadMessageMap);
                        ArrayList<Message> unReadMessages = sessionUnreadMessageMap.get(singleSessionUnique);
                        unReadMessages = null != unReadMessages ? unReadMessages : new ArrayList<>();
                        int index = unReadMessages.indexOf(message);
                        if (index >= 0) {//Replace
                            unReadMessages.remove(index);
                            unReadMessages.add(index, message);
                        } else {
                            unReadMessages.add(message);
                        }
                        sessionUnreadMessageMap.put(singleSessionUnique, unReadMessages);
                    }
                }
                //Cache into friend message
                if (null!=singleSessionUnique&&singleSessionUnique.length()>0) {//Only valid session unique can save
                    HashMap<String,HashMap<String,ArrayList<Message>>> friendMessageMap=mFriendMessageMap;
                    HashMap<String,ArrayList<Message>> loginFriendMessageMaps=friendMessageMap.get(loginUid);
                    loginFriendMessageMaps=null!=loginFriendMessageMaps?loginFriendMessageMaps:new HashMap<>();
                    friendMessageMap.put(loginUid,loginFriendMessageMaps);
                    ArrayList<Message> friendMessages = loginFriendMessageMaps.get(singleSessionUnique);
                    friendMessages = null != friendMessages ? friendMessages : new ArrayList<>();
                    int index=friendMessages.indexOf(message);
                    if (index>=0){//Replace
                        friendMessages.remove(index);
                        //Check if retry send succeed
                        if (message.getStatus()== Status.STATUS_SUCCEED&&message.getSendTry()>1){
                            friendMessages.add(message);//Just append to last while retry send succeed
                        }else{
                            friendMessages.add(index,message);
                        }
                    }else{
                        friendMessages.add(message);
                    }
                    loginFriendMessageMaps.put(singleSessionUnique,friendMessages);
                    Logger.D("Cache friend message.");
                }
                return save(null,null , "After cache friend message.");
            case MessageType.MESSAGETYPE_GROUP:
                HashMap<String,HashMap<String,ArrayList<Message>>> groupMessageMap=mGroupMessageMap;
                HashMap<String,ArrayList<Message>> loginGroupMessageMap=groupMessageMap.get(loginUid);
                loginGroupMessageMap=null!=loginGroupMessageMap?loginGroupMessageMap:new HashMap<>();
                groupMessageMap.put(loginUid, loginGroupMessageMap);
                String groupType=message.getGroupType();
                String groupId=message.getGroupId();
                String groupSessionUnique=null!=groupType&&null!=groupId&&groupType.length()>0&&groupId.
                        length()>0?createSessionUnique(new Group(groupId,groupType)):null;
                if (null==groupSessionUnique||groupSessionUnique.length()<=0){
                    Logger.W("Fail cache group message while fail create session unique.");
                }else{//Save group message
                    ArrayList<Message> groupMessages=loginGroupMessageMap.get(groupSessionUnique);
                    groupMessages=null!=groupMessages?groupMessages:new ArrayList<>();
                    int index=groupMessages.indexOf(message);
                    if (index>=0){//Replace
                        groupMessages.remove(index);
                        if (message.getStatus()== Status.STATUS_SUCCEED&&message.getSendTry()>1){
                            groupMessages.add(message);//Just append to last while retry send succeed
                        }else{
                            groupMessages.add(index,message);
                        }
                    }else{
                        groupMessages.add(message);
                    }
                    loginGroupMessageMap.put(groupSessionUnique,groupMessages);
                }
                return true;
        }
        return false;
    }

    public boolean removeGroupMessages(String loginUid,String debug){
        if (null==loginUid||loginUid.length()<=0){
            return false;
        }
        Logger.D("Remove group message "+(null!=debug?debug:"."));
        HashMap<String,HashMap<String,ArrayList<Message>>> groupMessageMap=mGroupMessageMap;
        return null!=groupMessageMap&&null!=groupMessageMap.remove(loginUid);
    }

    public final ArrayList<Message> getGroupCachedMessage(String loginUid,Group group, int from, int size, String debug){
        if (null==loginUid||loginUid.length()<=0||null==group){
            return null;
        }
        String groupSessionUnique=createSessionUnique(group);
        if (null==groupSessionUnique||groupSessionUnique.length()<=0){
            return null;
        }
        HashMap<String,HashMap<String,ArrayList<Message>>> friendMessageMap=mGroupMessageMap;
        HashMap<String,ArrayList<Message>> messageMap=null!=friendMessageMap?friendMessageMap.get(loginUid):null;
        return null!=messageMap?messageMap.get(groupSessionUnique):null;
    }

    public final ArrayList<Message> getFriendCachedMessage(String loginUid,String uid, int from, int size, String debug){
        if (null==loginUid||loginUid.length()<=0||null==uid||uid.length()<=0){
            return null;
        }
        HashMap<String,HashMap<String,ArrayList<Message>>> friendMessageMap=mFriendMessageMap;
        HashMap<String,ArrayList<Message>> messageMap=null!=friendMessageMap?friendMessageMap.get(loginUid):null;
        return null!=messageMap?messageMap.get(uid):null;
    }

    public ArrayList<String> getFriendRecentContact(String loginUid) {
        if (null == loginUid || loginUid.length() <= 0 ) {
            return null;
        }
        HashMap<String, ArrayList<String>> recentChatMap=mRecentChatMap;
        return null!=recentChatMap?recentChatMap.get(loginUid):null;
    }

    public final boolean deleteFriendMessage(String loginUid,String uid,String debug){
        if (null==loginUid||loginUid.length()<=0||null==uid||uid.length()<=0){
            return false;
        }
        HashMap<String,HashMap<String,ArrayList<Message>>> friendMessageMap=mFriendMessageMap;
        HashMap<String,ArrayList<Message>> messageMap=null!=friendMessageMap?friendMessageMap.get(loginUid):null;
        ArrayList<Message> messages=null!=messageMap?messageMap.get(uid):null;
        if (null!=messages&&null!=messageMap.remove(uid)){
            return save(null, null, "After delete friend message.");
        }
        return false;
    }

    public final boolean deleteCacheUnreadMessageExceptUser(String loginUid,Collection<User> users,String debug){
          if (null==loginUid||loginUid.length()<=0||null==users){
              return false;
          }
          HashMap<String,HashMap<String,ArrayList<Message>>> unreadMessageMap=mUnReadMessageMap;
          final HashMap<String,ArrayList<Message>> messageMap=null!=unreadMessageMap?unreadMessageMap.get(loginUid):null;
          if (null!=messageMap){
              final HashMap<String,ArrayList<Message>> tempMessageMap=new HashMap<>(messageMap.size());
              synchronized (messageMap){
                  tempMessageMap.putAll(messageMap);
              }
              final List<String> keepUniqueIds=new ArrayList<>();
              if (null!=users&&users.size()>0){
                  for (User child:users) {//Iterator all need keep
                      String uniqueId=null!=child?createSessionUnique(child):null;
                      if (null!=uniqueId&&uniqueId.length()>0){
                          keepUniqueIds.add(uniqueId);
                      }
                  }
              }
              boolean changed=false;
              synchronized (messageMap){
                  Set<String> messageSet=tempMessageMap.keySet();
                  for (String child:messageSet) {
                      if (null!=child&&!keepUniqueIds.contains(child)){
                          changed=true;
                          messageMap.remove(child);//Not friend,remove it
                          notifyEvent(Event.EVENT_FRIEND_CHAT_SESSION_UNREAD_MESSAGE_LIST_CHANGE, child);
                      }
                  }  
              }
              return changed&&save(null, null, debug);
          }
          return false;
      }

    public final boolean deleteCacheMessageExceptUser(String loginUid,Collection users,String debug){
        if (null==loginUid||loginUid.length()<=0||null==users){
            return false;
        }
        HashMap<String,HashMap<String,ArrayList<Message>>> friendMessageMap=mFriendMessageMap;
        HashMap<String,ArrayList<Message>> messageMap=null!=friendMessageMap?friendMessageMap.get(loginUid):null;
        if (null!=messageMap){
            boolean changed=false;
            synchronized (messageMap){
                int sessionSize=messageMap.size();
                final HashMap<String,ArrayList<Message>> newMessageMap=new HashMap<>(sessionSize);
                newMessageMap.putAll(messageMap);//Cache into temp
                messageMap.clear();
                changed=true;
                for (Object child:users) {
                    child=null!=child&&child instanceof User?((User)child).getId():child;
                    if (null!=child&&child  instanceof String&&((String)child).length()>0){
                        final String singleSessionUnique=createSessionUnique(new User((String)child));
                        ArrayList<Message> messages=null!=singleSessionUnique&&singleSessionUnique.length()>0?newMessageMap.get(singleSessionUnique):null;
                        if (null!=messages&&messages.size()>0){
                            messageMap.put(singleSessionUnique, messages);
                        }
                    }
                }
            }
            return changed&&save(null, null, debug);
        }
        return false;
    }

    public boolean deleteFriendRecentContact(String loginUid, Collection toUid,String debug){
        if (null==toUid||toUid.size()<=0){
            return false;
        }
        if (null==loginUid||loginUid.length()<=0){
            Logger.W("Can't delete friend recent contact while login uid or to uid invalid.");
            return false;
        }
        HashMap<String, ArrayList<String>> recentChatMap=mRecentChatMap;
        if (null==recentChatMap||recentChatMap.size()<=0){
            return false;
        }
        ArrayList<String> recentList=recentChatMap.get(loginUid);
        if (null==recentList||recentList.size()<=0){
            return false;//Not need delete while NONE
        }
        boolean changed=false;
        for (Object child:toUid) {
            child=null!=child&&child instanceof User?((User)child).getId():child;
            if (null!=child&&child instanceof String&&((String)child).length()>0 &&recentList.remove(child)){
                changed=true;
            }
        }
        return changed&&save(null, null,"After friend recent contact delete.");
    }

    public final ArrayList<AddFriendRequest> getAddFriendCachedRequest(String loginUid,int from, int size,String debug){
        if (null==loginUid||loginUid.length()<=0){
            Logger.W("Can't get add friend request while login uid invalid "+(null!=debug?debug:"."));
            return null;
        }
        HashMap<String,ArrayList<AddFriendRequest>> addFriendRequestMap=mAddFriendRequestMap;
        return null!=addFriendRequestMap?addFriendRequestMap.get(loginUid):null;
    }

    private boolean cacheFriendRecentContact(String loginUid,String toUid){
        if (null==loginUid||null==toUid||loginUid.length()<=0||toUid.length()<=0){
            Logger.W("Can't cache friend recent chat while login uid or to uid invalid.");
            return false;
        }
        if (loginUid.equals(toUid)) {
            return false;
        }
        HashMap<String, ArrayList<String>> recentChatMap=mRecentChatMap;
        recentChatMap=null!=recentChatMap?recentChatMap:new HashMap<>();
        ArrayList<String> loginRecent=null!=recentChatMap?recentChatMap.get(loginUid):null;
        loginRecent=null!=loginRecent?loginRecent:new ArrayList<>();
        loginRecent.remove(toUid);//Remove
        loginRecent.add(0,toUid);//Add
        recentChatMap.put(loginUid,loginRecent);
        Logger.D("Cache friend recent chat.");
        return save(null,null,"After cache friend recent contact.");
    }

    private boolean cacheAddRequest(AddFriendRequest request,String debug){
        if (null==request){
            Logger.W("Can't cache friend add request while request invalid.");
            return false;
        }
        final String loginUid=request.getLoginUid();
        final String fromUid=request.getFromUid();
        if (null==loginUid||null==fromUid||loginUid.length()<=0||fromUid.length()<=0){
            Logger.W("Can't cache friend add request while login uid or to uid invalid.");
            return false;
        }
        if (loginUid.equals(fromUid)) {
            Logger.W("Can't cache friend add request while login uid equals from uid.");
            return false;
        }
        HashMap<String,ArrayList<AddFriendRequest>> addFriendRequestMap=mAddFriendRequestMap;
        ArrayList<AddFriendRequest> requests=addFriendRequestMap.get(loginUid);
        requests=null!=requests?requests:new ArrayList<>();
        requests.remove(request);
        requests.add(request);
        addFriendRequestMap.put(loginUid, requests);
        return save(Event.EVENT_FRIEND_ADD_REQUEST_LIST_CHANGE, requests.size(),"After cache friend add request.");
    }

    public final boolean deleteCachedAddRequest(String loginUid,List<AddFriendRequest> list,String debug){
        if (null==loginUid||null==list||loginUid.length()<=0||list.size()<=0){
            Logger.W("Can't delete cached friend add request while login uid or to uid invalid.");
            return false;
        }
        HashMap<String,ArrayList<AddFriendRequest>> addFriendRequestMap=mAddFriendRequestMap;
        ArrayList<AddFriendRequest> requests=null!=addFriendRequestMap?addFriendRequestMap.get(loginUid):null;
        return null!=requests&&requests.removeAll(list)&&save(Event.EVENT_FRIEND_ADD_REQUEST_LIST_CHANGE,
                requests.size(),"After delete friend add request.");
    }

    private String createSessionUnique(Session session){
        if (null==session){
            return null;
        }else if (session instanceof User){
            return ((User)session).getId();
        }else if (session instanceof Group){
            return ((Group)session).getGroupUnique();
        }
        return null;
    }

    private boolean save(Integer event,Object arg,String debug){
        CacheTool cacheTool=mCacheTool;
        if (null==cacheTool){
            Logger.W("Can't save cache while NULL.");
            return false;
        }
        Logger.D("Save cache "+(null!=debug?debug:"."));
        ThreadPoolUtils.getInstance().execute(()-> {
            cacheTool.put(LABEL_RECENT_CHAT, mRecentChatMap);
            cacheTool.put(LABEL_UNREAD_MESSAGE, mUnReadMessageMap);
            cacheTool.put(LABEL_ADD_FRIEND_REQUEST, mAddFriendRequestMap);
//            cacheTool.put(LABEL_GROUP_MESSAGE, mGroupMessageMap);
            cacheTool.put(LABEL_FRIEND_MESSAGE, mFriendMessageMap);
            if (null!=event){
                notifyEvent(event, arg);
            }
        });
        return true;
    }
}
