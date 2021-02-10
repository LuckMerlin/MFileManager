package com.csdk.server.cache;

import com.csdk.api.bean.ChatConfig;
import com.csdk.api.bean.Group;
import com.csdk.api.bean.LoginAuth;
import com.csdk.api.bean.Menu;
import com.csdk.api.bean.Reply;
import com.csdk.data.ServerConfigure;
import com.csdk.api.bean.User;
import com.csdk.api.core.Code;
import com.csdk.api.core.Event;
import com.csdk.data.AuthenticationRequest;
import com.csdk.debug.Logger;
import com.csdk.server.Configure;
import com.csdk.server.Matchable;
import com.csdk.server.OnChannelLoadFinish;
import com.csdk.server.OnServerConfigureLoadFinish;
import com.csdk.server.OnUserProfileLoadFinish;
import com.csdk.server.data.Bool;
import com.csdk.server.data.Json;
import com.csdk.api.core.Label;
import com.csdk.server.http.HttpRequest;
import com.csdk.server.http.OnHttpFinish;
import com.csdk.server.http.OnUserIdLoadSyncFinish;
import com.csdk.server.http.OnUserLoadSyncFinish;
import com.csdk.server.util.MatchInvoker;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import okhttp3.Call;

/**
 * Create LuckMerlin
 * Date 16:40 2020/12/14
 * TODO
 */
public class HttpCaller extends CSDKCache {
    private List<User> mBlockUsers;
    private List<User> mUsers;
    private List<User> mFriends;
    private List<Menu<Group>> mChannels;

    public HttpCaller(String cachePath){
        super(cachePath);
        //Test
//        Handler handler=new Handler(Looper.getMainLooper());
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mFriends=new TestUserList().generate((int)(new Random().nextFloat()*50));
//                notifyEvent(Event.EVENT_FRIEND_LIST_CHANGED, null);
//                handler.postDelayed(this, 5000);
//            }
//        }, 10000);
    }

    public User getUserProfileByUid(String uid, String debug){
        return null!=uid&&uid.length()>0?getUserProfile((Object arg)-> {
            String childUid=null!=arg&&arg instanceof User?((User)arg).getId():null;
            return null!=childUid&&childUid.equals(uid)? Matchable.MATCHED:Matchable.CONTINUE;
        },debug):null;
    }

    public User getUserProfile(Matchable matchable,String debug){
        List<User> users=null!=matchable?mUsers:null;
        if (null!=users){
            synchronized (users){
                if (users.size()>0){
                    for (User child:users) {
                        Integer matched=null!=child?matchable.onMatch(child):null;
                        if (null==matched||matched==Matchable.CONTINUE){
                            continue;
                        }else if (matched==Matchable.MATCHED){
                            return child;
                        }else if (matched==Matchable.BREAK){
                            return null;
                        }
                    }
                }
            }
        }
        return null;
    }

    public boolean fetchUserProfile(LoginAuth loginAuth, String httpServerUri, String uid, OnUserProfileLoadFinish callback, String debug){
        if (null!=callback){
            if (null==uid||uid.length()<=0){
                callback.onUserProfileLoadFinish(false, "Uid is NONE.", null);
                return false;
            }
            User user=getUserProfileByUid(uid,debug);
            if (null!=user){
                callback.onUserProfileLoadFinish(true, "Cached user.", user);
                return true;
            }
            return loadUserProfileByUid(loginAuth, httpServerUri, uid, callback, debug);
        }
        return false;
    }

    public boolean fetchUserProfileByUids(LoginAuth loginAuth, String httpServerUri, List<String> uids, OnUserLoadSyncFinish callback, String debug) {
        if (null != callback) {
            if (null==uids||uids.size()<=0){
                callback.onUserLoadSyncFinish(false, Code.CODE_FAIL,"Uid args empty.",null);
                return false;
            }
            List<User> result=new ArrayList<>();
            List<String> needLoad=new ArrayList<>();
            User user=null;
            for (String child:uids) {
                if (null!=child&&child.length()>0){
                    if (null==(user=getUserProfileByUid(child,debug))){
                        needLoad.add(child);
                    }else{
                        result.add(user);
                    }
                }
            }
            if (null==needLoad||needLoad.size()<=0){//Not need load again
                callback.onUserLoadSyncFinish(true,Code.CODE_SUCCEED,"All from cached.",result);
                return true;
            }
            return loadUserById(loginAuth, httpServerUri, (boolean succeed, int code, String note, List<User> data)-> {
                if (succeed&&code==Code.CODE_SUCCEED&&null!=data){
                    result.addAll(data);
                }
                callback.onUserLoadSyncFinish(succeed,code,note,result);
            },debug,needLoad);//Load not cached user from server
        }
        return false;
    }

    public final List<User> getCachedFriends(String loginUid, Matchable matcher, int max, String debug) {
        List<User> users=mFriends;
        synchronized (users) {
            return new MatchInvoker().invokeMatch(users, matcher, max);
        }
    }

    public final boolean updateUserInfo(LoginAuth loginAuth,String httpServerUri,AuthenticationRequest request, String debug){
        if (null==request){
            return false;
        }
        Logger.D("Update user info."+(null!=debug?debug:"."));
        Json profile=new Json();
        profile.putSafe(Label.LABEL_VIP_LEVEL,request.getVipLevel()).putSafe(Label.LABEL_ROLE_LEVEL,request.getRoleLevel());
        return new HttpRequest().callHttpRequest(loginAuth,httpServerUri,"/api/user/profile/update",
                new Json().putSafe(Label.LABEL_AVATAR_URL,request.getAvatarUrl())
                        .putSafe(Label.LABEL_GENDER,request.getGender())
                        .putSafe(Label.LABEL_ROLE_NAME,request.getRoleName())
                        .putSafe(Label.LABEL_PROFILE,profile), new OnHttpFinish<Reply>(){
                @Override
                protected void onSyncFinish(boolean succeed, Call call, String note, Reply data) {
                    Logger.D("Finish update user info."+succeed+" "+(null!=data?data.getCode():-1));
                }}, debug);
    }

    public List<User> getBlockUsers(Matchable matchable,int max){
        return new MatchInvoker().invokeMatch(mBlockUsers, matchable, max);
    }

    public final boolean loadUserById(LoginAuth loginAuth, String httpServerUri, final OnUserLoadSyncFinish callback, String debug, String ...uids){
        JSONArray array=null;
        if (null!=uids&&uids.length>0){
            array=new JSONArray();
            for (String child:uids ) {
                if (null!=child){
                    array.put(child);
                }
            }
        }
        return loadUserById(loginAuth, httpServerUri,callback, debug, array);
    }

    public final boolean loadUserById(LoginAuth loginAuth, String httpServerUri,final OnUserLoadSyncFinish callback, String debug,JSONArray array){
        return new HttpRequest().callHttpRequest(loginAuth,httpServerUri,"/api/user/find", new Json().putSafe("uids",array), new OnHttpFinish<Reply<List<User>>>(){
            @Override
            protected void onSyncFinish(boolean succeed, Call call, String note, Reply<List<User>> data) {
                int code=null!=data?data.getCode(): Code.CODE_FAIL;
                List<User> succeedData=null!=data?data.getSucceedData():null;
                notifyUserLoadFinish(succeed, code, note, succeedData,callback);
            }
        }, debug);
    }

    public final boolean loadUserProfileByUid(LoginAuth loginAuth,String httpServerUri,String uid, OnUserProfileLoadFinish callback,String debug){
        try {
            if (null==uid||uid.length()<=0){
                if (null!=callback){
                    callback.onUserProfileLoadFinish(false,"Uid invalid.",null);
                }
                return false;
            }
            return new HttpRequest().callHttpRequest(loginAuth,httpServerUri,"/api/user/profile",new Json().putSafe(Label.LABEL_UID,
                    Long.parseLong(uid)).putSafe(Label.LABEL_SHOW_ASSETS, Bool.YES),new OnHttpFinish<Reply<User>>(){
                @Override
                protected void onSyncFinish(boolean succeed, Call call, String note, Reply<User> data) {
                    super.onSyncFinish(succeed, call, note, data);
                    User user=succeed&&null!=data&& data.getCode()==Code.CODE_SUCCEED?data.getSucceedData():null;
                    if (null!=user){
                        List<User> list=new ArrayList<>(1);
                        list.add(user);
                        replaceUserCache(list,"After user profile load.");
                        if (null!=callback){
                            callback.onUserProfileLoadFinish(succeed, note, user);
                        }
                        notifyEvent(Event.EVENT_USER_CHANGED,user);
                    }
                }
            },debug);
        }catch (Exception e){

        }
        return false;
    }

    private final boolean replaceUserCache(List<User> list,String debug){
        if (null!=list&&list.size()>0){
            List<User> friends=mFriends;
            if (null!=friends){
                synchronized (friends) {
                    friends.removeAll(list);
                    friends.addAll(list);
                }
            }
            List<User> users=mUsers;
            users=null==users?(mUsers=new ArrayList<>()):users;
            synchronized (users) {
                users.removeAll(list);
                users.addAll(list);
                return true;
            }
        }
        return false;
    }

    public final boolean loadGroupByType(LoginAuth loginAuth, String httpServerUri,String groupType,String debug){
        return new HttpRequest().callHttpRequest(loginAuth, httpServerUri,"/api/user/blocked/list",
                new Json().putSafe(Label.LABEL_GROUP_TYPE,groupType),new OnHttpFinish<>(),debug);
    }

    public final boolean loadChannels(LoginAuth loginAuth, String httpServerUri, OnChannelLoadFinish callback, final String debug) {
        Logger.D("Load menu channels " + debug);
        return new HttpRequest().callHttpRequest(loginAuth, httpServerUri,"/api/product/menus", null, new OnHttpFinish<Reply<List<Menu<Group>>>>(){
                    @Override
                    protected void onSyncFinish(boolean succeed, Call call, String note, Reply<List<Menu<Group>>> data) {
                        super.onSyncFinish(succeed, call, note, data);
                        Logger.D("Finish load menu channels "+debug+" "+succeed);
                        List<Menu<Group>> menuList=null;
                        if (succeed && null != data && data.isSucceed()) {
                            List<Menu<Group>> menus=data.getData();
                            int size=null!=menus?menus.size():-1;
                            if (size>0){
                                menuList=new ArrayList<>(size);
                                menuList.addAll(menus);
                            }
                            mChannels = menus;
                            notifyEvent(Event.EVENT_CHANNEL_LIST_CHANGED, menuList);
                        }
                        if (null!=callback){
                            callback.onChannelLoadFinish(succeed, null!=data?data.getCode():Code.CODE_FAIL,
                                    null!=note?note:null!=data?data.getMsg():null, menuList);
                        }
                    }
                }
        , debug);
    }

    public final boolean loadServerConfigure(ChatConfig config,String httpServerUri, OnServerConfigureLoadFinish callback, String debug){
        Configure configure=Configure.getInstance();
        Json args=new Json();
        String productId=null;String productKey=null;
        if (null!=config){
            args.putSafe(Label.LABEL_PRODUCT_ID,productId=config.getProductId());
            productKey=config.getProductKey();
        }
        if (null!=configure){
            args.putSafe(Label.LABEL_DEVICE_ID,configure.getDeviceId()).putSafe(Label.LABEL_LANGUAGE,configure.getSystemLanguage())
                    .putSafe(Label.LABEL_SDK_VERSION, configure.getVersion());
        }
        return new HttpRequest().callHttpRequest(productId,productKey, httpServerUri, "/api/sys/config", args, new OnHttpFinish<Reply<ServerConfigure>>(){
            @Override
            protected void onSyncFinish(boolean succeed, Call call, String note, Reply<ServerConfigure> data) {
                super.onSyncFinish(succeed, call, note, data);
                succeed=succeed&&null!=data&&data.isSucceed();
                if (null!=callback){
                    callback.onServerConfigureLoadFinish(succeed,note,null!=data?data.getData():null);
                }
            }} , debug);
    }


    public final boolean loadBlockUsers(LoginAuth loginAuth, String httpServerUri,OnUserLoadSyncFinish callback,String debug){
        return null!=loginAuth&&new HttpRequest().callHttpRequest(loginAuth, httpServerUri,"/api/user/blocked/list",
                null, new OnHttpFinish<Reply<List<User>>>(){
            @Override
            protected void onFinish(boolean succeed, Call call, String note, Reply<List<User>> data) {
                List<User> list=null;
                if (succeed && null != data && data.isSucceed()) {
                    list = data.getData();
                    mBlockUsers=list;
                    deleteFriendRecentContact(null!=loginAuth?loginAuth.getUid():null,list,"After block list load finish.");
                    notifyEvent(Event.EVENT_BLOCK_FRIEND_LIST_CHANGED,null);
                }
                if (null!=callback){
                    callback.onUserLoadSyncFinish(succeed, null!=data?data.getCode():Code.CODE_FAIL, note, list);
                }
            }
        } , debug);
    }

    public final boolean loadUserById(LoginAuth loginAuth,String httpServerUri, final OnUserLoadSyncFinish callback,
                                      String debug, Collection<String> uids){
        JSONArray array=null;
        if (null!=uids&&uids.size()>0){
            array=new JSONArray();
            for (String child:uids ) {
                if (null!=child){
                    array.put(child);
                }
            }
        }
        return loadUserById(loginAuth,httpServerUri, callback, debug, array);
    }

    public final boolean loadUidByRoleId(LoginAuth loginAuth, String httpServerUri, String roleId,
                                         OnUserIdLoadSyncFinish callback, String debug){
        return null!=callback&&new HttpRequest().callHttpRequest(loginAuth,httpServerUri,"/api/user/uidByRoleId",
                new Json().putSafe(Label.LABEL_ROLE_ID,roleId), new OnHttpFinish<Reply<String>>(){
                    @Override
                    protected void onSyncFinish(boolean succeed, Call call, String note, Reply<String> data) {
                        String uid=succeed&&null!=data&&data.getCode()==Code.CODE_SUCCEED?data.getData():null;
                        succeed=null!=uid&&!uid.equals("0");
                        callback.onUserLoadSyncFinish(succeed, succeed?Code.CODE_SUCCEED:Code.CODE_FAIL, note,uid);
                    }
                }, debug);
    }

    public final boolean fetchUserIdByRoleId(LoginAuth loginAuth,String httpServerUri,OnUserIdLoadSyncFinish callback, String roleId, String debug) {
        return loadUidByRoleId(loginAuth, httpServerUri, roleId,callback, debug);
    }

    public final List<Menu<Group>> getChannels(Matchable matchable, int max){
        return new MatchInvoker().invokeMatch(mChannels, matchable, max);
    }

    public final boolean loadFriendList(LoginAuth loginAuth, String httpServerUri,OnUserLoadSyncFinish callback,String debug){
        return null!=loginAuth&&new HttpRequest().callHttpRequest(loginAuth, httpServerUri, "/api/user/friends",
                new Json().putSafe(Label.LABEL_CONTAINS_BLOCKED, Bool.YES), new OnHttpFinish<Reply<List<User>>>() {
                    @Override
                    protected void onSyncFinish(boolean succeed, Call call, String note, Reply<List<User>> data) {
                        List<User> list=null;
                        if (succeed && null != data && data.isSucceed()) {
                            list = data.getData();
                            replaceUserCache(list,"After friend list load.");
                            mFriends=list;
                            String loginUid=null!=loginAuth?loginAuth.getUid():null;
                            list=null!=list?list:new ArrayList<>();
                            deleteCacheMessageExceptUser(loginUid,list,"After friend list load.");
                            deleteCacheUnreadMessageExceptUser(loginUid,list,"After friend list load.");
                            notifyEvent(Event.EVENT_FRIEND_LIST_CHANGED, list);
                        }
                        notifyUserLoadFinish(succeed, null!=data?data.getCode():Code.CODE_FAIL, note, list,callback);
                    }
                }, debug);
    }

    private void notifyUserLoadFinish(boolean succeed,int code,String note,List<User> data,OnUserLoadSyncFinish callback){
        if (null!=callback){
            callback.onUserLoadSyncFinish(succeed, code, note, data);
        }
    }
}
