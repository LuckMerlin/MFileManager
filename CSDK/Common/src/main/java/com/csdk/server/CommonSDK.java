package com.csdk.server;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.csdk.api.bean.AddFriendRequest;
import com.csdk.api.bean.CSDKAction;
import com.csdk.api.bean.ChatBaseInfo;
import com.csdk.api.bean.ChatConfig;
import com.csdk.api.bean.Link;
import com.csdk.api.bean.User;
import com.csdk.api.common.Api;
import com.csdk.api.common.CommonApi;
import com.csdk.api.common.OnCSDKListener;
import com.csdk.api.core.Code;
import com.csdk.api.core.Debug;
import com.csdk.api.core.Listener;
import com.csdk.api.core.OnSendFinish;
import com.csdk.api.core.Operation;
import com.csdk.api.core.Page;
import com.csdk.api.ui.HomeModel;
import com.csdk.api.ui.Model;
import com.csdk.debug.Logger;
import com.csdk.ui.ContentModel;
import com.csdk.data.AuthenticationRequest;
import com.csdk.server.data.Json;
import com.csdk.api.core.Label;
import com.csdk.server.socket.HeroSocket;
import com.csdk.server.util.Utils;
import com.csdk.ui.DataBindingUtil;
import com.csdk.ui.HomeModelLoader;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Create LuckMerlin
 * Date 18:29 2021/1/13
 * TODO
 */
public final class CommonSDK implements CommonApi {
    private HeroSocket mHeroSocket;
    private Listener mListener;
    private ChatBaseInfo mChatBaseInfo;
    private WeakReference<Context> mContext;
    private View mContentView;
    private ChatConfig mChatConfig;

    public CommonSDK(){
        Thread.setDefaultUncaughtExceptionHandler((Thread t, Throwable e)-> {
            Logger.E("Global exception."+e);
            if (null!=e){ e.printStackTrace(); }
        });
    }

    @Override
    public int intoGroupWithArgs(String args,OnSendFinish callback) {
        Json json=null!=args&&args.length()>0? Json.create(args):null;
        if(null==json){
            Logger.W("Fail into group with args while arg invalid.");
            return Code.CODE_PARAMS_INVALID;
        }
        final HeroSocket heroSocket=mHeroSocket;
        if (!isInitialed()||null==heroSocket){
            Logger.W("Fail into group with args while NONE init.");
            return Code.CODE_NONE_INITIAL;
        }
        Object customIdObj=json.opt(Label.LABEL_CUSTOM_ID);
        String customId=null!=customIdObj?customIdObj.toString():null;
        if (null==customId||customId.length()<=0){
            Logger.W("Fail into group with args while group custom id invalid.");
            return Code.CODE_PARAMS_INVALID;
        }
        String groupType=json.optString(Label.LABEL_GROUP_TYPE, null);
        if (null==groupType||groupType.length()<=0){
            Logger.W("Fail into group with args while group custom type invalid.");
            return Code.CODE_PARAMS_INVALID;
        }
        String groupName=json.optString(Label.LABEL_GROUP_NAME, null);
        return heroSocket.joinCustomGroup(customId,groupType,groupName,callback,"While api call.")?Code.CODE_SUCCEED:Code.CODE_FAIL;
    }

    @Override
    public int quitGroupWithArgs(String args,OnSendFinish callback) {
        Json json=null!=args&&args.length()>0? Json.create(args):null;
        if(null==json){
            Logger.W("Fail quit group with args while arg invalid.");
            return Code.CODE_PARAMS_INVALID;
        }
        final HeroSocket heroSocket=mHeroSocket;
        if (!isInitialed()||null==heroSocket){
            Logger.W("Fail quit group with args while NONE init.");
            return Code.CODE_NONE_INITIAL;
        }
        String customId=json.optString(Label.LABEL_CUSTOM_ID, null);
        if (null==customId||customId.length()<=0){
            Logger.W("Fail quit group with args while group custom id invalid.");
            return Code.CODE_PARAMS_INVALID;
        }
        String groupType=json.optString(Label.LABEL_GROUP_TYPE, null);
        if (null==groupType||groupType.length()<=0){
            Logger.W("Fail quit group with args while group custom type invalid.");
            return Code.CODE_PARAMS_INVALID;
        }
        return heroSocket.quitCustomGroup(customId,groupType,callback,"While api call.")?Code.CODE_SUCCEED:Code.CODE_FAIL;
    }

    @Override
    public int initial(Context context, ChatConfig config) {
        if (null==config){
            Logger.W("Fail initial csdk while config args invalid.");
            return Code.CODE_PARAMS_INVALID;
        }
        if (null==context){
            Logger.W("Fail initial csdk while context args NULL.");
            return Code.CODE_PARAMS_INVALID;
        }
        final String productKey=config.getProductKey();
        if (null==productKey||productKey.length()<=0){
            Logger.W("Fail initial csdk while config product key invalid.");
            return Code.CODE_PARAMS_INVALID;
        }
        final String productId=config.getProductId();
        if (null==productId||productId.length()<=0){
            Logger.W("Fail initial csdk while config product id invalid.");
            return Code.CODE_PARAMS_INVALID;
        }
        mContext=new WeakReference<>(context);
        HeroSocket heroSocket=mHeroSocket;
        if (null==heroSocket) {
            String deviceNumber = Utils.getDeviceID(context);
            String language = new Language().getSystemLanguage(context);
            Configure configure = Configure.getInstance();
            String buildVersion = null;
            String sdkVersion = null;
            if (null != configure) {
                configure.settSystemLanguage(language);
                configure.setDeviceId(deviceNumber);
                buildVersion = configure.getBuildVersion();
                sdkVersion = configure.getVersion();
            }
            Logger.D("Now, Initial csdk.language=" + language + " dev=" + deviceNumber + " bv=" + buildVersion + " sv=" + sdkVersion);
            String cacheDirPath = getDiskCacheDir(context);
            mHeroSocket=heroSocket=new HeroSocket(context,cacheDirPath,config){
                @Override
                public boolean notifyActionChange(CSDKAction action, String args, String debug) {
                    Listener listener=mListener;
                    if (null!=listener&&listener instanceof OnCSDKListener){
                        post(()->((OnCSDKListener)listener).onCsdkActionChange(action, args));
                        return true;
                    }
                    return super.notifyActionChange(action, args, debug);
                }

                @Override
                protected boolean onOpenLinkInterrupt(Link link, String loginRoleId, String debug) {
                    Listener listener=mListener;
                    if (null!=link&&null!=listener&&listener instanceof OnCSDKListener){
                        Json json=new Json().putSafe(Label.LABEL_CUSTOM_ID,link.getCustomId()).
                                putSafe(Label.LABEL_FROM_ROLE_ID,link.getFromRoleId()).putSafe(Label.LABEL_GROUP_TYPE,link.getGroupType()).
                                putSafe(Label.LABEL_TITLE,link.getTitle()).putSafe(Label.LABEL_TYPE,link.getType()).
                                putSafe(Label.LABEL_USER_NAME,link.getUserName()).putSafe(Label.LABEL_DATA,link.getData());
                        post(()->((OnCSDKListener)listener).onCsdkActionChange(CSDKAction.ACTION_CLICK_LINK, json.toString()));
                        return true;
                    }
                    return true;
                }

                @Override
                protected boolean onBlockFriendInterrupt(boolean block, String toUid, String loginRoleId, String debug) {
                    Listener listener=mListener;
                    if (null!=listener&&listener instanceof OnCSDKListener){
                        Json json=new Json().putSafe(Label.LABEL_FROM_ROLE_ID,loginRoleId).
                                putSafe(Label.LABEL_TO_UID,toUid);
                        post(()->((OnCSDKListener)listener).onCsdkActionChange(block?
                                CSDKAction.ACTION_FRIEND_BLOCK:CSDKAction.ACTION_FRIEND_UNBLOCK, json.toString()));
                        return true;
                    }
                    return true;
                }

                @Override
                protected boolean onDeleteFriendInterrupt(String toUid, String loginRoleId, String debug) {
                    Listener listener=mListener;
                    if (null!=listener&&listener instanceof OnCSDKListener){
                        Json json=new Json().putSafe(Label.LABEL_FROM_ROLE_ID,loginRoleId).
                                putSafe(Label.LABEL_TO_UID,toUid);
                        post(()->((OnCSDKListener)listener).onCsdkActionChange(CSDKAction.ACTION_FRIEND_DELETE, json.toString()));
                        return true;
                    }
                    return true;
                }

                @Override
                public boolean onAddFriendAcceptInterrupt(List<AddFriendRequest> messages, String loginRoleId, String debug) {
//                    Listener listener=mListener;
//                    if (null!=messages&&messages.size()>0&&null!=listener&&listener instanceof OnCSDKListener){
//                        JSONArray array=new JSONArray();
//                        for (AddFriendRequest child:messages) {
//                            if (null==child){
//                                continue;
//                            }
//                            array.put(new Json().putSafe(Label.LABEL_EXTRA,new Json().putSafe(Label.
//                                    LABEL_FROM,child.getFromName())).putSafe(Label.LABEL_FROM_ROLE_ID,child.getUserRoleId()).
//                                    putSafe(Label.LABEL_TO_ROLE_ID,child.getLoginUid()));
//                        }
//                        post(()->((OnCSDKListener)listener).onCsdkActionChange(CSDKAction.ACTION_FRIEND_ADD, array.toString()));
//                    }
                    return true;
                }
            };
            mChatConfig=config;
            if (!heroSocket.isLogin(null)&&null!=mChatBaseInfo){
                login(mChatBaseInfo, "While initial with base info set.");
            }
            Logger.D("Succeed initial csdk.");
            return Code.CODE_SUCCEED;
        }
        return Code.CODE_ALREADY_DONE;
    }

    @Override
    public int setChatBaseInfo(ChatBaseInfo baseInfo) {
        if (null==baseInfo){
            Logger.W("Fail set base info while NULL.");
            return Code.CODE_PARAMS_INVALID;
        }
        mChatBaseInfo=baseInfo;
        if (null!=mHeroSocket){//If already initialed,Now to login switch role
            login(baseInfo, "While api set base info.");
        }
        return Code.CODE_SUCCEED;
    }

    @Override
    public int setOnCSDKListener(Listener listener) {
        if (null!=listener){
            mListener =listener;
            return Code.CODE_SUCCEED;
        }
        return Code.CODE_FAIL;
    }

    @Override
    public final String getVersion() {
        Configure configure=Configure.getInstance();
        return null!=configure?configure.getVersion():null;
    }

    @Override
    public int notifyActionChange(CSDKAction action, String args,OnSendFinish callback) {
        Json argJson=null!=action&&null!=args&&args.length()>0? Json.create(args):null;
        if(null==argJson){
            Logger.W("Can't notify action change while args invalid.");
            return Code.CODE_PARAMS_INVALID;
        }
        switch (action){
            case ACTION_FRIEND_DELETE:
                return deleteFriendByUid(argJson.optString(Label.LABEL_TO_UID, null), callback);
            case ACTION_FRIEND_BLOCK:
                return blockFriendByUid(true,argJson.optString(Label.LABEL_TO_UID, null), callback);
            case ACTION_FRIEND_UNBLOCK:
                return blockFriendByUid(false,argJson.optString(Label.LABEL_TO_UID, null), callback);
            case ACTION_FRIEND_ADD:
                return applyAddFriendByUid(argJson.optString(Label.LABEL_TO_UID, null),
                        argJson.optString(Label.LABEL_FROM, null), callback);
        }
        return Code.CODE_FAIL;
    }

    public final int agreeAddFriend(String actionToken, OnSendFinish callback){
        HeroSocket heroSocket=mHeroSocket;
        if (null==heroSocket){
            Logger.W("Can't accept add friend while NONE initial.");
            notifySendFinish(false, "None initial.", null, callback);
            return Code.CODE_NONE_INITIAL;
        }
        return heroSocket.agreeAddFriend(actionToken,callback,"While api call.")?Code.CODE_SUCCEED:Code.CODE_FAIL;
    }

    public final int applyAddFriendByUid(String toUid,String address,OnSendFinish callback){
        HeroSocket heroSocket=mHeroSocket;
        if (null==heroSocket){
            Logger.W("Can't add friend while NONE initial.");
            notifySendFinish(false, "None initial.", null, callback);
            return Code.CODE_NONE_INITIAL;
        }
        return heroSocket.applyAddFriendByUid(toUid,address,callback,"While api call.")?Code.CODE_SUCCEED:Code.CODE_FAIL;
    }

    public final int quitCustomGroup(String customId, String groupType, OnSendFinish callback){
        HeroSocket heroSocket=mHeroSocket;
        if (null==heroSocket){
            Logger.W("Can't quit custom group while NONE initial.");
            notifySendFinish(false, "None initial.", null, callback);
            return Code.CODE_NONE_INITIAL;
        }
        return heroSocket.quitCustomGroup(customId,groupType,callback,"While api call.")?Code.CODE_SUCCEED:Code.CODE_FAIL;
    }

    public final int agreeInviteCreateGroup(String actionToken,String groupType,OnSendFinish callback){
        HeroSocket heroSocket=mHeroSocket;
        if (null==heroSocket){
            Logger.W("Can't agree invite create group while NONE initial.");
            notifySendFinish(false, "None initial.", null, callback);
            return Code.CODE_NONE_INITIAL;
        }
        return heroSocket.agreeInviteCreateGroup(actionToken,groupType,callback,"While api call.")?Code.CODE_SUCCEED:Code.CODE_FAIL;
    }

    public final int agreeJoinGroupApply(String actionToken,OnSendFinish callback){
        HeroSocket heroSocket=mHeroSocket;
        if (null==heroSocket){
            Logger.W("Can't agree join group's apply while NONE initial.");
            notifySendFinish(false, "None initial.", null, callback);
            return Code.CODE_NONE_INITIAL;
        }
        return heroSocket.agreeJoinGroupApply(actionToken,callback,"While api call.")?Code.CODE_SUCCEED:Code.CODE_FAIL;
    }

    public final int deleteFriendByUid(String toUid, OnSendFinish callback){
        HeroSocket heroSocket=mHeroSocket;
        if (null==heroSocket){
            Logger.W("Can't delete friend while NONE initial.");
            notifySendFinish(false, "None initial.", null, callback);
            return Code.CODE_NONE_INITIAL;
        }
        return heroSocket.deleteFriendByUid(toUid,callback,"While api call.")?Code.CODE_SUCCEED:Code.CODE_FAIL;
    }

    public int inviteCreateGroup(String groupType, String toUid, OnSendFinish callback){
        HeroSocket heroSocket=mHeroSocket;
        if (null==heroSocket){
            Logger.W("Can't invite create group while NONE initial.");
            notifySendFinish(false, "None initial.", null, callback);
            return Code.CODE_NONE_INITIAL;
        }
        return heroSocket.inviteCreateGroup(groupType,toUid,callback,"While api call.")?Code.CODE_SUCCEED:Code.CODE_FAIL;
    }

    public int applyJoinCustomGroup(String customId,String groupType,OnSendFinish callback){
        HeroSocket heroSocket=mHeroSocket;
        if (null==heroSocket){
            Logger.W("Can't invite create group while NONE initial.");
            notifySendFinish(false, "None initial.", null, callback);
            return Code.CODE_NONE_INITIAL;
        }
        return heroSocket.applyJoinCustomGroup(customId,groupType,callback,"While api call.")?Code.CODE_SUCCEED:Code.CODE_FAIL;
    }

    public int blockFriendByUid(boolean block,String toUid, OnSendFinish callback){
        HeroSocket heroSocket=mHeroSocket;
        if (null==heroSocket){
            Logger.W("Can't block friend by uid while NONE initial.");
            notifySendFinish(false, "None initial.", null, callback);
            return Code.CODE_NONE_INITIAL;
        }
        return heroSocket.blockFriendByUid(block,toUid,callback,"While api call.")?Code.CODE_SUCCEED:Code.CODE_FAIL;
    }

    @Override
    public int setContentView(Object contentView, FrameLayout.LayoutParams params){
        View currentContentView=mContentView;
        mContentView=null;
        if (null!=currentContentView&&removeFromParent(currentContentView)){
            Logger.D("Removed current content view while open chat ui.");
        }
        if (null==contentView&&null!=currentContentView){
            return Code.CODE_SUCCEED;
        }else if (null!=contentView){
            Context context=getContext();
            if (null==context||!(context instanceof Activity)){
                Debug.W("Can't set content view while context not activity.");
                return Code.CODE_FAIL;
            }
            if (null!=contentView){
                return new ContentModel().setContentView((Activity)context,contentView,params);
            }
        }
        return Code.CODE_FAIL;
    }

    @Override
    public int openChatUi(boolean outline) {
        Looper looper=Looper.myLooper();
        if (null==looper||looper!=Looper.getMainLooper()){
            Logger.W("Fail open chat ui while call in sub thread.");
            return Code.CODE_FAIL;
        }
        final HeroSocket heroSocket=getHeroSocket();
        if (!isInitialed()||null==heroSocket){
            Logger.W("Fail open chat ui while NONE init.");
            return Code.CODE_NONE_INITIAL;
        }
        Context context=getContext();
        if (null==context){
            Logger.W("Fail open chat ui while context invalid.");
            return Code.CODE_PARAMS_INVALID;
        }
        if (!(context instanceof Activity)){
            Logger.W("Fail open chat ui while context not activity instance.");
            return Code.CODE_PARAMS_INVALID;
        }
        View contentView=mContentView;
        if (null==contentView){//Try load content view
            Model homeModel=new HomeModelLoader().getHomeModel(context,mApi,"While api call open chat ui.");
            if (null!=homeModel){
                int code=setContentView(homeModel,new FrameLayout.LayoutParams(FrameLayout.LayoutParams.
                        MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));//Set just create home view as content view
                Debug.D((code==Code.CODE_SUCCEED?"Succeed":"Fail")+" create home model.");
            }
        }
        if (null!=(contentView=mContentView)){
            Model model=null!=contentView?new DataBindingUtil().findFirstModel(contentView):null;
            if (null!=model&&model instanceof HomeModel){
                ((HomeModel)model).enableOutline(outline);
            }
            if (contentView.getVisibility()!=View.VISIBLE) {
                contentView.setVisibility(View.VISIBLE);
            }
            return Code.CODE_SUCCEED;
        }
        return Code.CODE_FAIL;
    }

    @Override
    public int closeChatUi() {
        Looper looper=Looper.myLooper();
        if (null==looper||looper!=Looper.getMainLooper()){
            Logger.W("Fail close chat ui while call in sub thread.");
            return Code.CODE_FAIL;
        }
        View currentContent=mContentView;
        if (null!=currentContent&&currentContent.getVisibility()==View.VISIBLE){
            currentContent.setVisibility(View.GONE);
            return Code.CODE_SUCCEED;
        }
        return Code.CODE_FAIL;
    }

    private boolean removeFromParent(View view){
        ViewParent parent=null!=view?view.getParent():null;
        if (null!=parent&&parent instanceof ViewGroup){
            ((ViewGroup)parent).removeView(view);
            return true;
        }
        return false;
    }

    private final boolean isInitialed() {
        return null!=mHeroSocket;
    }

    public HeroSocket getHeroSocket() {
        return mHeroSocket;
    }

    private final Context getContext(){
        WeakReference<Context> reference=mContext;
        return null!=reference?reference.get():null;
    }

    private final int login(ChatBaseInfo baseInfo,String debug){
        final HeroSocket heroSocket=mHeroSocket;
        if (null==heroSocket){
            Logger.W("Fail login csdk while NONE initial.");
            return Code.CODE_NONE_INITIAL;
        }
        if (null==baseInfo){
            Logger.W("Fail login csdk while config base info invalid.");
            return Code.CODE_PARAMS_INVALID;
        }
        String roleId=baseInfo.getRoleId();
        if(null==roleId||roleId.length()<=0){
            Logger.W("Fail login csdk while config role id invalid.");
            return Code.CODE_PARAMS_INVALID;
        }
        ChatConfig config=mChatConfig;
        if (null==config){
            Logger.W("Fail login csdk while config args invalid.");
            return Code.CODE_PARAMS_INVALID;
        }
        final String productId=config.getProductId();
        if (null==productId||productId.length()<=0){
            Logger.W("Fail login csdk while config product id invalid.");
            return Code.CODE_PARAMS_INVALID;
        }
        int[] accepts=config.getAccepts();
        accepts=null!=accepts?accepts:new int[]{Operation.SEND_RECEIPT, Operation.SYSTEM_MESSAGE};
        Configure configure=Configure.getInstance();
        String deviceNumber= null!=configure?configure.getDeviceId():null;
        String sdkVersion= null!=configure?configure.getVersion():null;
        AuthenticationRequest request=new AuthenticationRequest(productId,sdkVersion,deviceNumber,baseInfo, accepts);
        Logger.D("Now, To login csdk "+(null!=debug?debug:"."));
        return heroSocket.connect(request,debug)?Code.CODE_SUCCEED:Code.CODE_FAIL;
    }

    private final String getDiskCacheDir(Context context) {
        if (null!=context){
            File cacheFile = null;
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
                cacheFile = context.getExternalCacheDir();
            }
            cacheFile = null!=cacheFile?cacheFile:context.getCacheDir();
            return null!=cacheFile?cacheFile.getAbsolutePath():null;
        }
        return null;
    }

    //////////////////////////////////Api///////////////////////

    private final Api mApi=new CommonSDKApi(){
        @Override
        public int setContentView(final Object contentViewObj, FrameLayout.LayoutParams params) {
            if (null==contentViewObj){
                Logger.W("Can't set csdk content view while content view NULL.");
                return Code.CODE_PARAMS_INVALID;
            }
            return CommonSDK.this.setContentView(contentViewObj,params);
        }

        @Override
        public Page<Object, User> getFriends(int from, int size) {
            return null;
        }

        @Override
        HeroSocket getHeroSocket() {
            return mHeroSocket;
        }
    };

    @Override
    public final Api getApi() {
        return mApi;
    }

    private final boolean notifySendFinish(boolean succeed, String note, Object reply, OnSendFinish callback){
        if (null!=callback){
            callback.onSendFinish(succeed,note,reply);
            return true;
        }
        return false;
    }
}
