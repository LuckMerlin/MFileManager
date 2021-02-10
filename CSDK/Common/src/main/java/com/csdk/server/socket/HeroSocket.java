package com.csdk.server.socket;

import android.content.Context;

import com.csdk.api.bean.AddFriendRequest;
import com.csdk.api.bean.CSDKAction;
import com.csdk.api.bean.CSDKGroup;
import com.csdk.api.bean.CSDKRole;
import com.csdk.api.bean.ChatConfig;
import com.csdk.api.bean.FriendRelation;
import com.csdk.api.bean.Group;
import com.csdk.api.bean.Link;
import com.csdk.api.bean.Menu;
import com.csdk.api.bean.Message;
import com.csdk.api.bean.Reply;
import com.csdk.api.bean.User;
import com.csdk.api.core.Code;
import com.csdk.api.core.ContentType;
import com.csdk.api.core.MessageType;
import com.csdk.api.core.OnMessageReply;
import com.csdk.api.core.OnSendFinish;
import com.csdk.api.core.Operation;
import com.csdk.debug.Logger;
import com.csdk.server.MessageObject;
import com.csdk.server.Notify;
import com.csdk.server.data.Frame;
import com.csdk.server.data.Json;
import com.csdk.api.core.Label;
import com.csdk.server.http.HttpRequest;
import com.csdk.server.http.OnHttpCallback;
import com.csdk.server.http.OnHttpFinish;
import com.csdk.server.http.OnUserIdLoadSyncFinish;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Create LuckMerlin
 * Date 15:58 2020/8/5
 * TODO
 */
public class HeroSocket extends IMSocket {

    public HeroSocket(Context context,String cacheDir){
        this(context,cacheDir,null);
    }

    public HeroSocket(Context context,String cacheDir, ChatConfig config){
        super(context,cacheDir,config);
    }

    public boolean onAddFriendAcceptInterrupt(List<AddFriendRequest> messages,String loginRoleId,String debug){
        //Do nothing
        return false;
    }

    protected boolean onPersonalPageShowInterrupt(Object roleObj, String loginRoleId,String debug){
        return false;
    }

    protected boolean onBlockFriendInterrupt(boolean block, String toUid, String loginRoleId, String debug) {
        //Do nothing
        return false;
    }

    protected boolean onGiveFriendCoinInterrupt(User user, String loginRoleId, String debug) {
        //Do nothing
        return false;
    }

    protected boolean onDeleteFriendInterrupt(String toUid, String loginRoleId, String debug) {
        //Do nothing
        return false;
    }

    protected boolean onOpenLinkInterrupt(Link link,String loginRoleId,String debug){
        //Do nothing
        return false;
    }

    @Override
    protected void onFrameReceived(Frame frame) {
        super.onFrameReceived(frame);
        if (null!=frame){
            long operation=frame.getOperation();
            if (operation== Operation.SYSTEM_FORCE_EXIT){
                setKeepAlive(false, "After force exit message received.");
            }
        }
    }

    @Override
    protected void onMenuListLoadSucceed(List<Menu<Group>> groups, String debug) {
        super.onMenuListLoadSucceed(groups, debug);
        //Auto entry room
        if (null!=groups&&groups.size()>0){
            for (Menu<Group> menu:groups) {
                Group group=null!=menu&&menu.isVisible()?menu.getGroup():null;
                if (null!=group){
                    String groupId=group.getId();
                    if (null==groupId||groupId.length()<=0){
                        Logger.W("Skip auto entry room while group id invalid After menu load finish.");
                        continue;
                    }
                    String groupTitle=group.getTitle();
                    entryRoom(true, group.getType(), groupId,(boolean succeed, String note, Frame frame)-> {
                        Reply reply=succeed&&null!=frame?frame.getBodyReply():null;
                        Logger.D((null!=reply&&reply.isSucceed()?"Succeed":"Failed")+" entry room "+groupTitle);
                    }, "After menu load succeed.");
                }
            }
        }
    }

    public final boolean sendTextMessage(CharSequence text, Map<String, Object> extra, Object to, String debug){
        if (null == text || text.length() <= 0) {
            return false;
        }
        Message message = new Message(Operation.SEND,null, null != text ? text.toString() : null, ContentType.CONTENTTYPE_TEXT, null, null);
        if (null != extra && extra.size() > 0) {
            message.putExtra(extra);
        }
        return sendMessage(message, to, null, null, debug);
    }

    public final boolean sendMessage(MessageObject message, Object to, String charSet, OnMessageReply callback, String debug){
        if (null!=to&&null!=message&&message instanceof Message){
            Message msg=(Message)message;
            if (to instanceof Group){
                Group group=(Group)to;
                msg.setFromUid(getLoginUserId());
                msg.setGroupType(group.getType());
                msg.setGroupId(group.getId());
                msg.setMsgType(MessageType.MESSAGETYPE_GROUP);
            }else if (to instanceof CSDKGroup){
                CSDKGroup group=(CSDKGroup)to;
                msg.setFromUid(getLoginUserId());
                msg.setGroupType(group.getType());
                msg.setGroupId(group.getId());
                msg.setMsgType(MessageType.MESSAGETYPE_GROUP);
            }else if (to instanceof User){
                msg.setFromUid(getLoginUserId());
                msg.setMsgType(MessageType.MESSAGETYPE_SINGLE);
                msg.append2Uids(((User)to).getId());
            }else if (to instanceof CSDKRole){
                return fetchUserIdByRoleId((boolean succeed, int code, String note, String data)-> {
                    if (null!=callback&&(!succeed||code!= Code.CODE_SUCCEED||null==data||data.length()<=0)){
                        Logger.W("Fail send message while fetch uid fail.");
                        callback.onMessageReplied(false, "Fail fetch uid.", null);
                    }else{
                        sendMessage(message, data, charSet, callback, debug);
                    }
                }, ((CSDKRole) to).getRoleId(), "While send message by role id.");
            }else if (to instanceof String){
                msg.setFromUid(getLoginUserId());
                msg.setMsgType(MessageType.MESSAGETYPE_SINGLE);
                msg.append2Uids(((String)to));
            }
        }
        return sendMessage(message,charSet,callback,debug);
    }

    public final boolean sendMessage(CharSequence text, Map<String, Object> extra, Object to, OnMessageReply callback,String debug) {
        if (null == text || text.length() <= 0) {
            return false;
        }else if (text.length()>=150){
            return false;
        }
        Message message = new Message(Operation.SEND,null, null != text ? text.toString() : null, ContentType.CONTENTTYPE_TEXT, null, null);
        if (null != extra && extra.size() > 0) {
            message.putExtra(extra);
        }
        return sendMessage(message, to, null, null, debug);
    }

    public final boolean uploadFileToCloud(File file, JSONObject args, OnHttpCallback callback, String debug){
        return callHttpRequest("/api/file/upload", args, file,callback,debug)  ;
    }

    public final boolean callHttpRequest(String routeUri, JSONObject args, File file, OnHttpCallback callback, String debug){
        return new HttpRequest().callHttpRequest(getLoginAuth(),getHttpServerUri(null), routeUri, args, file,callback, debug);
    }

    public final boolean updateRoleStatus(Integer roleStatus, String statusName,String debug){
        return callHttpRequest("/api/user/status/update",new Json().putSafe(Label.LABEL_STATUS,
                roleStatus).putSafe(Label.LABEL_STATUS_NAME,statusName),null, new OnHttpFinish<Reply>(),debug);
    }

    public final boolean sendVoiceMessage(Object voiceFile, Object to,long duration, String translate, OnMessageReply callback,String debug) {
        if (null==voiceFile){
            Logger.W("Fail send voice message while voice file NONE.");
            return false;
        }
        if (voiceFile instanceof File){
            File filePath=(File)voiceFile;
            if (!filePath.exists() || filePath.length() <= 0) {
                Logger.W("Fail send voice message while voice file invalid.");
                return false;
            }
            final Message voiceMessage = new Message(Operation.SEND,null, filePath.getAbsolutePath(),
                    ContentType.CONTENTTYPE_VOICE, null, null);
            final int durationSec = (int) duration / 1000;
            voiceMessage.putExtra(Label.LABEL_TRANSLATION,translate);
            voiceMessage.putExtra(Label.LABEL_DURATION, Integer.toString(durationSec <= 0 ? 0 : durationSec));
            return uploadFileToCloud(filePath,null,new OnHttpFinish<Reply<String>>(){
                @Override
                protected void onFinish(boolean succeed, Call call, String note, Reply<String> data) {
                    String cloudUrl= succeed&&null!=data?data.getSucceedData():null;
                    voiceMessage.setContent(null != cloudUrl && cloudUrl.length() > 0 ? cloudUrl : null);
                    sendMessage(voiceMessage, to,null, callback, debug);
                }
            },debug);
        }else if (voiceFile instanceof String){
            final Message voiceMessage = new Message(Operation.SEND,null, (String) voiceFile,
                    ContentType.CONTENTTYPE_VOICE, null, null);
            final int durationSec = (int) duration / 1000;
            voiceMessage.putExtra(Label.LABEL_TRANSLATION,translate);
            voiceMessage.putExtra(Label.LABEL_DURATION, Integer.toString(durationSec <= 0 ? 0 : durationSec));
            voiceMessage.setContent((String)voiceFile);
            return sendMessage(voiceMessage, to,null, callback, debug);
        }
        Logger.W("Fail send voice message while voice file not support.");
        return false;
    }

    /**
     * @deprecated
     */
    public final boolean addFriendFrom(String toUid, String fromAddress, OnMessageReply callback, String debug){
        Map<String, String> extra=null;
        if (null!=fromAddress&&fromAddress.length()>0){
            extra=new HashMap<>(1);
            extra.put(Label.LABEL_FROM, fromAddress);
        }
        return addFriend(toUid,extra, callback, debug);
    }

    public final boolean addFriend(String toUid, Map<String, String> extra, OnMessageReply callback, String debug){
        if (null==toUid||toUid.length()<=0){
            Logger.W("Fail add friend while UID invalid "+debug);
            return notifyReply(false, "Uid invalid", null, callback)&&false;
        }
        return sendText(Operation.ADD_FRIEND,new Json().putSafe(Label.LABEL_TO_UID,toUid).putMapSafe(Label.LABEL_EXTRA,extra),callback,debug);
    }

    public final boolean acceptJoinTeam(String actionToken,OnMessageReply callback,String debug){
        if (null==actionToken||actionToken.length()<=0){
            Logger.W("Fail accept join team while token invalid "+debug);
            return notifyReply(false, "Token invalid", null, callback)&&false;
        }
        return sendText(Operation.INVITE_JOIN_TEAM_AGREE,new Json().putSafe(Label.LABEL_ACTION_TOKEN,actionToken),callback,debug);
    }

    public final boolean showPersonalPage(Object roleObj,String debug){
        if (null==roleObj){
            Logger.W("Fail show personal page while role obj NULL "+debug);
            return false;
        }
        if (onPersonalPageShowInterrupt(roleObj,getLoginRoleId(), debug)){
            Logger.W("Interrupted show personal page  "+(null!=debug?debug:"."));
            return true;
        }
        Logger.D("Inner show personal page.");
        return false;
    }

    public final boolean acceptAddFriend(List<AddFriendRequest> messages, OnMessageReply callback, String debug){
        if (null==messages||messages.size()<=0){
            Logger.W("Fail accept add friend while message EMPTY "+debug);
            return notifyReply(false, "Token invalid", null, callback)&&false;
        }
        if (isDispatchFriendRelation(FriendRelation.ACCEPT_ADD_FRIEND)&&
                onAddFriendAcceptInterrupt(messages,getLoginRoleId(), debug)){
            Logger.W("Interrupted inner accept add friend "+(null!=debug?debug:"."));
            if (null!=callback){
                callback.onMessageReplied(true,null,null);
            }
            return true;
        }
        String actionToken=null;
        for (AddFriendRequest child:messages) {
            if (null!=(actionToken=(null!=child?child.getActionToken():null))){
                sendText(Operation.ADD_FRIEND_AGREE,new Json().putSafe(Label.LABEL_ACTION_TOKEN,actionToken),callback,debug);
            }
        }
        return true;
    }

    public final boolean entryRoom(boolean entry,String roomType,String roomId,OnMessageReply callback,String debug){
        if (null==roomId||roomId.length()<=0){
            Logger.W("Fail entry room while roomId invalid "+debug);
            return notifyReply(false, "RoomType or roomId invalid", null, callback)&&false;
        }
        Logger.D((entry?"Entry":"Leave")+" room "+roomType+" "+roomId+" "+debug);
        return sendText(entry?Operation.JOIN_ROOM:Operation.LEAVE_ROOM,new Json().putSafe("roomId",(null!=roomType?roomType:"")+"@"+roomId),callback,debug);
    }

    public final boolean openLink(Link link,String debug){
        if (null==link){
            Logger.W("Fail open link while link NULL "+(null!=debug?debug:"."));
            return false;
        }
        //Check if need dispatch open link to CP
        if (onOpenLinkInterrupt(link,getLoginRoleId(), debug)){
            Logger.W("Interrupted inner open link "+(null!=debug?debug:"."));
            return true;
        }
        Logger.D("Open link inner.");
        return false;
    }

    public final boolean deleteFriend(String toUid, OnMessageReply callback, String debug){
        if (null==toUid||toUid.length()<=0){
            Logger.W("Fail delete friend while UID invalid "+(null!=debug?debug:"."));
            return notifyReply(false, "Uid invalid", null, callback)&&false;
        }
        if (isDispatchFriendRelation(FriendRelation.DELETE_FRIEND)&&onDeleteFriendInterrupt(toUid,getLoginRoleId(), debug)){
            Logger.W("Interrupted inner delete friends "+(null!=debug?debug:"."));
            return notifyReply(true, "Request interrupted.", null, callback)&&false;
        }
        Logger.D("Delete friend.");
        return sendText(Operation.DELETE_FRIEND,new Json().putSafe(Label.LABEL_TO_UID,toUid),callback,debug);
    }

    public final boolean giveFriendCoin(User user,String debug){
        //Check if need dispatch block to CP
        if (onGiveFriendCoinInterrupt(user,getLoginRoleId(), debug)){
            Logger.W("Interrupted inner give friend coin "+(null!=debug?debug:"."));
            return notifyReply(true, "Give friend coin interrupted.", null, null)&&false;
        }
        Logger.D("Give friend coin.");
        return false;
    }

    public final boolean blockFriend(boolean block,String toUid, OnMessageReply callback, String debug){
        if (null==toUid||toUid.length()<=0){
            Logger.W("Fail block friend while UID invalid "+(null!=debug?debug:"."));
            return notifyReply(false, "Uid invalid", null, callback)&&false;
        }
         //Check if need dispatch block to CP
        if (isDispatchFriendRelation(FriendRelation.BLOCK_FRIEND)&&
                onBlockFriendInterrupt(block,toUid,getLoginRoleId(), debug)){
            Logger.W("Interrupted inner block friends "+(null!=debug?debug:"."));
            return notifyReply(true, "Request interrupted.", null, callback)&&false;
        }
        Logger.D((block?"Block":"Unblock")+" friend.");
        return sendText(block?Operation.BLOCK_FRIEND:Operation.UNBLOCK_FRIEND,new Json().putSafe(Label.LABEL_TO_UID,toUid),callback,debug);
    }

    /**
     * @deprecated
     */
    public final boolean createGroup(String groupId,String groupType,String title,OnMessageReply callback,String debug){
        if (null==groupType||groupType.length()<=0){
            Logger.W("Fail create while group type invalid "+debug);
            return notifyReply(false, "Group type invalid", null, callback)&&false;
        }
        return sendText(Operation.CREATE_GROUP,new Json().putSafe(Label.LABEL_CUSTOM_ID,groupId).
                putSafe(Label.LABEL_GROUP_TYPE,groupType).putSafe(Label.LABEL_TITLE,title),callback,debug);
    }

    public final boolean createCustomGroup(String customId,String groupType,String title,OnMessageReply callback,String debug){
        if (null==groupType||groupType.length()<=0){
            Logger.W("Fail create custom group while group type invalid "+debug);
            return notifyReply(false, "Group type invalid", null, callback)&&false;
        }
        return sendText(Operation.CREATE_GROUP,new Json().putSafe(Label.LABEL_CUSTOM_ID,customId).
                putSafe(Label.LABEL_GROUP_TYPE,groupType).putSafe(Label.LABEL_TITLE,title),callback,debug);
    }

    public final boolean quitGroup(String groupId,String groupType,OnMessageReply callback,String debug){
        if (null==groupId||groupId.length()<=0){
            Logger.W("Fail join group while group id invalid "+debug);
            return notifyReply(false, "Group id invalid", null, callback)&&false;
        }
        Logger.D("Quit group "+groupId+" "+groupType);
        return sendText(Operation.DISMISS_GROUP,new Json().putSafe("groupId",groupId).putSafe("groupType",groupType),callback,debug);
    }

    public final boolean joinGroup(String groupId,String groupType,OnMessageReply callback,String debug){
        if (null==groupId||groupId.length()<=0){
            Logger.W("Fail join group while group id invalid "+debug);
            return notifyReply(false, "Group id invalid", null, callback)&&false;
        }
        Logger.D("Join group "+groupId+" "+groupType);
        return sendText(Operation.JOIN_GROUP,new Json().putSafe("groupId",groupId).putSafe("groupType", groupType),callback,debug);
    }

    public final boolean dismissCustomGroup(String groupType,String groupName,OnMessageReply callback,String debug){
        return dismissCustomGroup(groupType, null, groupName, callback, debug);
    }

    public final boolean dismissCustomGroup(String groupType,String groupId,String groupName,OnMessageReply callback,String debug){
        if (null==groupType||groupType.length()<=0){
            Logger.W("Fail dismiss custom group while group type invalid "+debug);
            return notifyReply(false, "Custom group type invalid", null, callback)&&false;
        }
        Logger.D("Dismiss custom "+groupType);
        return sendText(Operation.QUIT_GROUP,new Json().putSafe(Label.LABEL_GROUP_NAME,groupName)
                .putSafe(Label.LABEL_GROUP_ID, groupId).putSafe(Label.LABEL_GROUP_TYPE, groupType),callback,debug);
    }

    public final boolean dismissGroup(String groupId,String groupType,OnMessageReply callback,String debug){
        if (null==groupId||groupId.length()<=0){
            Logger.W("Fail dismiss group while group id invalid "+debug);
            return notifyReply(false, "Group id invalid", null, callback)&&false;
        }
        Logger.D("Dismiss group "+groupId+" "+groupType);
        return sendText(Operation.QUIT_GROUP,new Json().putSafe("groupId",groupId).putSafe("groupType", groupType),callback,debug);
    }

    public final boolean joinOrDismissGroup(boolean join,String groupId,String groupType,OnMessageReply callback,String debug){
        if (null==groupId||groupId.length()<=0){
            Logger.W("Fail join or dismiss group while group id invalid "+join+" "+debug);
            return notifyReply(false, "Group id invalid", null, callback)&&false;
        }
        return join?joinGroup(groupId, groupType, callback, debug):dismissGroup(groupId, groupType, callback, debug);
    }

    public final boolean acceptJoinGroup(String actionToken,OnMessageReply callback,String debug){
        if (null==actionToken||actionToken.length()<=0){
            Logger.W("Fail accept join group request while group id invalid "+debug);
            return notifyReply(false, "Group id invalid", null, callback)&&false;
        }
        Logger.D("Accept join group request "+actionToken);
        return sendText(Operation.JOIN_GROUP_AGREE,new Json().putSafe(Label.LABEL_ACTION_TOKEN,actionToken),callback,debug);
    }

    public final boolean acceptJoinGroupInvite(String actionToken,OnMessageReply callback,String debug){
        if (null==actionToken||actionToken.length()<=0){
            Logger.W("Fail accept join group while group id invalid "+debug);
            return notifyReply(false, "Group id invalid", null, callback)&&false;
        }
        Logger.D("Accept join group "+actionToken);
        return sendText(Operation.INVITE_JOIN_GROUP_AGREE,new Json().putSafe(Label.LABEL_ACTION_TOKEN,actionToken),callback,debug);
    }

    public final boolean dismissTeam(String groupId,String groupType,OnMessageReply callback,String debug){
        if (null==groupId||groupId.length()<=0){
            Logger.W("Fail dismiss team while group id invalid "+debug);
            return notifyReply(false, "Group id invalid", null, callback)&&false;
        }
        Logger.D("Dismiss team "+groupId+" "+groupType);
        return sendText(Operation.QUIT_GROUP,new Json().putSafe("groupId",groupId).putSafe("groupType", groupType),callback,debug);
    }

    public final boolean quitTeam(String groupId,String groupType,OnMessageReply callback,String debug){
        if (null==groupId||groupId.length()<=0){
            Logger.W("Fail quit team while group id invalid "+debug);
            return notifyReply(false, "Group id invalid", null, callback)&&false;
        }
        Logger.D("Quit team "+groupId+" "+groupType);
        return sendText(Operation.QUIT_TEAM,new Json().putSafe("groupId",groupId).putSafe("groupType", groupType),callback,debug);
    }

    public final boolean inviteJoinTeam(String toUid,OnMessageReply callback,String debug){//None team exist
        return sendText(Operation.INVITE_JOIN_TEAM,new Json().putSafe(Label.LABEL_TO_UID, toUid),callback,debug);
    }

    public final boolean fetchUserRoleId(User user, OnUserIdLoadSyncFinish callback, String debug){
        if (null!=callback) {
            String roleId= null!=user?user.getRoleId():null;
            if (null!=roleId&&roleId.length()>0){
                callback.onUserLoadSyncFinish(true, Code.CODE_SUCCEED, "From user.",roleId);
                return true;
            }
            String uid= null!=user?user.getId():null;
            if (null!=uid&&uid.length()>0){
                List<String> uids=new ArrayList<>(1);
                uids.add(uid);
                return fetchUserById((boolean succeed, int code, String note, List<User> data)-> {
                    User newUser=null!=data&&data.size()>0?data.get(0):null;
                    callback.onUserLoadSyncFinish(succeed, code, note,null!=newUser?newUser.getRoleId():null);
                }, debug, uids);
            }
            callback.onUserLoadSyncFinish(false,Code.CODE_FAIL,"User uid invalid.",null);
            return true;
        }
        return false;
    }

    public final boolean inviteJoinGroup(String groupId,String groupType,String toUid,OnMessageReply callback,String debug){
        if (null==groupId||groupId.length()<=0){
            Logger.W("Fail invite join group while group id invalid "+debug);
            return notifyReply(false, "Group id invalid", null, callback)&&false;
        }
        if (null==groupType||groupType.length()<=0){
            Logger.W("Fail invite join group while group type invalid "+debug);
            return notifyReply(false, "Group type invalid", null, callback)&&false;
        }
        if (null==toUid||toUid.length()<=0){
            Logger.W("Fail invite join group while to uid invalid "+debug);
            return notifyReply(false, "To uid invalid", null, callback)&&false;
        }
        return sendText(Operation.INVITE_JOIN_GROUP,new Json().putSafe("groupType",groupType).putSafe("groupId",groupId).
                putSafe(Label.LABEL_TO_UID, toUid),callback,debug);
    }

    @Override
    protected void onSystemMessageReceived(Message message) {
        super.onSystemMessageReceived(message);
        if (null!=message){
            int notifyType=message.getNotifyType();
            Json json=null;
            CSDKAction csdkAction=null;
            if (notifyType==Notify.NOTIFY_INVITE_JOIN_INTO_GROUP){//10
                String actionToken=message.getActionToken();
//                        String avatarUrl=sysMessage.getExtraString(Label.LABEL_AVATAR_URL);
                String groupType=message.getExtraString(Label.LABEL_GROUP_TYPE);
//                        String fromUid=sysMessage.getExtraString(Label.LABEL_UID);
                String fromUserName=message.getExtraString(Label.LABEL_USER_NAME);
                json=new Json().putSafe(Label.LABEL_ACTION_TOKEN,actionToken).
                        putSafe(Label.LABEL_GROUP_TYPE,groupType).putSafe(Label.LABEL_USER_NAME,fromUserName);
                csdkAction=CSDKAction.ACTION_GROUP_APPLY;
            }else if (notifyType==Notify.NOTIFY_REQUEST_ADD_GROUP){//3
                //String groupId=message.getExtraString(Label.LABEL_GROUP_ID);
                json=new Json().putSafe(Label.LABEL_ACTION_TOKEN,message.getActionToken()).
                        putSafe(Label.LABEL_GROUP_TYPE,message.getExtraString(Label.LABEL_GROUP_TYPE)).
                        putSafe(Label.LABEL_AVATAR_URL,message.getExtraString(Label.LABEL_AVATAR_URL)).
                        putSafe(Label.LABEL_GROUP_CUSTOM_ID,message.getExtraString(Label.LABEL_GROUP_CUSTOM_ID)).
                        putSafe(Label.LABEL_TITLE,message.getExtraString(Label.LABEL_TITLE)).
                        putSafe(Label.LABEL_USER_NAME,message.getExtraString(Label.LABEL_USER_NAME));
                csdkAction = CSDKAction.ACTION_GROUP_INVITATION;
            }else if (notifyType==Notify.NOTIFY_ACCEPT_JOIN_INTO_GROUP){//13
                String uid=message.getExtraString(Label.LABEL_UID);
                String groupType=message.getExtraString(Label.LABEL_GROUP_TYPE);
                String customId=message.getExtraString(Label.LABEL_GROUP_CUSTOM_ID);
                String uname=message.getExtraString(Label.LABEL_USER_NAME);
                joinCustomGroup(customId, groupType, null,
                        (boolean succeed, String note, Object reply) ->{ },"After system event received.");
            }else if (notifyType==Notify.NOTIFY_GROUP_JOINED){//11

            }else if (notifyType==Notify.NOTIFY_JOIN_GROUP_REPLY_ACCEPTED){//4
                String uid=message.getExtraString(Label.LABEL_UID);
                String groupType=message.getExtraString(Label.LABEL_GROUP_TYPE);
                String customId=message.getExtraString(Label.LABEL_CUSTOM_ID);
                String uname=message.getExtraString(Label.LABEL_USER_NAME);
                json=new Json().putSafe(Label.LABEL_UID,uid).putSafe(Label.LABEL_GROUP_TYPE,groupType).
                putSafe(Label.LABEL_CUSTOM_ID,customId).putSafe(Label.LABEL_USER_NAME,uname);
                csdkAction = CSDKAction.ACTION_GROUP_JOIN;
            }else if (notifyType==Notify.NOTIFY_INVITE_JOIN_GROUP){//5
//                csdkAction = CSDKAction.ACTION_GROUP_APPLY;
            }
            if (null!=csdkAction){
                notifyActionChange(csdkAction,null!=json?json.toString():null,"After system event received.");
            }
        }
    }
    /////////////////////////////////////////////////////////////////////////
    public final boolean send(MessageObject message, Object to, String charSet, OnSendFinish callback, String debug){
        if (null!=to&&null!=message&&message instanceof Message){
            Message msg=(Message)message;
            if (to instanceof Group){
                Group group=(Group)to;
                msg.setFromUid(getLoginUserId());
                msg.setGroupType(group.getType());
                msg.setGroupId(group.getId());
                msg.setMsgType(MessageType.MESSAGETYPE_GROUP);
            }else if (to instanceof CSDKGroup){
                CSDKGroup group=(CSDKGroup)to;
                msg.setFromUid(getLoginUserId());
                msg.setGroupType(group.getType());
                msg.setGroupId(group.getId());
                msg.setMsgType(MessageType.MESSAGETYPE_GROUP);
            }else if (to instanceof User){
                msg.setFromUid(getLoginUserId());
                msg.setMsgType(MessageType.MESSAGETYPE_SINGLE);
                msg.append2Uids(((User)to).getId());
            }else if (to instanceof CSDKRole){
                return fetchUserIdByRoleId((boolean succeed, int code, String note, String data)-> {
                    if (null!=callback&&(!succeed||code!= Code.CODE_SUCCEED||null==data||data.length()<=0)){
                        Logger.W("Fail send message while fetch uid fail.");
                        callback.onSendFinish(false, "Fail fetch uid.", null);
                    }else{
                        send(message, data, charSet, callback, debug);
                    }
                }, ((CSDKRole) to).getRoleId(), "While send message by role id.");
            }else if (to instanceof String){
                msg.setFromUid(getLoginUserId());
                msg.setMsgType(MessageType.MESSAGETYPE_SINGLE);
                msg.append2Uids(((String)to));
            }
        }
        return sendMessage(message, charSet, new OnMessageReply() {
            @Override
            public void onMessageReplied(boolean succeed, String note, Frame frame) {
                if (null!=callback){
                    callback.onSendFinish(succeed, note, null!=frame?frame.getBodyReply():null);
                }
            }
        }, debug);
    }

    public final boolean blockFriendByUid(boolean block,String toUid, OnSendFinish callback, String debug){
        if (null==toUid||toUid.length()<=0){
            Logger.W("Fail block friend while UID invalid "+(null!=debug?debug:"."));
            return notifySendFinish(false, "Uid invalid", null, callback)&&false;
        }
        Logger.D((block?"Block":"Unblock")+" friend.");
        return sendText(block ? Operation.BLOCK_FRIEND : Operation.UNBLOCK_FRIEND, new Json().putSafe(Label.LABEL_TO_UID, toUid),
                (boolean succeed, String note, Frame frame)-> {
                    Reply reply=succeed&&null!=frame?frame.getBodyReply():null;
                    notifySendFinish(null!=reply&&reply.isSucceed(),note,reply,callback);
        }, debug);
    }

    public final boolean deleteFriendByUid(String toUid, OnSendFinish callback, String debug){
        if (null==toUid||toUid.length()<=0){
            Logger.W("Fail delete friend while UID invalid "+(null!=debug?debug:"."));
            return notifySendFinish(false, "Uid invalid", null, callback)&&false;
        }
        if (isDispatchFriendRelation(FriendRelation.DELETE_FRIEND)&&onDeleteFriendInterrupt(toUid,getLoginRoleId(), debug)){
            Logger.W("Interrupted inner delete friends "+(null!=debug?debug:"."));
            return notifySendFinish(true, "Request interrupted.", null, callback)&&false;
        }
        Logger.D("Delete friend.");
        return sendText(Operation.DELETE_FRIEND, new Json().putSafe(Label.LABEL_TO_UID, toUid),(boolean succeed, String note, Frame frame)->{
            Reply reply=succeed&&null!=frame?frame.getBodyReply():null;
            notifySendFinish(null!=reply&&reply.isSucceed(),note,reply,callback);
        }, debug);
    }

    public final boolean agreeAddFriend(String actionToken, OnSendFinish callback, String debug){
        if (null==actionToken||actionToken.length()<=0){
            Logger.W("Fail accept add friend while action token invalid "+(null!=debug?debug:"."));
            return notifySendFinish(false, "Action token invalid.", null, callback)&&false;
        }
        return sendText(Operation.ADD_FRIEND_AGREE,new Json().putSafe(Label.LABEL_ACTION_TOKEN,actionToken),
                (boolean succeed, String note, Frame frame) ->{
            Reply reply=succeed&&null!=frame?frame.getBodyReply():null;
            notifySendFinish(null!=reply&&reply.isSucceed(),note,reply,callback);
        },debug);
    }

    public final boolean applyAddFriendByUid(String toUid, String fromAddress, OnSendFinish callback, String debug){
        Map<String, String> extra=null;
        if (null!=fromAddress&&fromAddress.length()>0){
            extra=new HashMap<>(1);
            extra.put(Label.LABEL_FROM, fromAddress);
        }
        return addFriend(toUid,extra,(boolean succeed, String note, Frame frame) ->{
            Reply reply=succeed&&null!=frame?frame.getBodyReply():null;
            notifySendFinish(null!=reply&&reply.isSucceed(),note,reply,callback);
        }, debug);
    }

    public final boolean applyJoinCustomGroup(String customId,String groupType,OnSendFinish callback,String debug){
        if (null==customId||customId.length()<=0){
            Logger.W("Fail apply join custom group while group id invalid "+debug);
            return notifySendFinish(false, "Group custom id invalid", null, callback)&&false;
        }
        Logger.D("Apply join custom group.","Apply join custom "+customId+" "+groupType);
        return sendText(Operation.APPLY_JOIN_GROUP, new Json().putSafe(Label.LABEL_CUSTOM_ID, customId).putSafe
                (Label.LABEL_GROUP_TYPE, groupType), (boolean succeed, String note, Frame frame) ->{
            Reply reply=succeed&&null!=frame?frame.getBodyReply():null;
            notifySendFinish(null!=reply&&reply.isSucceed(),note,reply,callback);
        }, debug);
    }

    public final boolean quitCustomGroup(String customId,String groupType,OnSendFinish callback,String debug){
        if (null==customId||customId.length()<=0){
            Logger.W("Fail quit custom group while group id invalid "+debug);
            return notifySendFinish(false, "Group custom id invalid", null, callback)&&false;
        }
        Logger.D("Quit custom group.","Quit custom group "+customId+" "+groupType);
        return sendText(Operation.QUIT_GROUP, new Json().putSafe(Label.LABEL_CUSTOM_ID, customId).putSafe
                (Label.LABEL_GROUP_TYPE, groupType), (boolean succeed, String note, Frame frame) ->{
            Reply reply=succeed&&null!=frame?frame.getBodyReply():null;
            notifySendFinish(null!=reply&&reply.isSucceed(),note,reply,callback);
        }, debug);
    }

    public final boolean joinCustomGroup(String customId,String groupType,String groupName,OnSendFinish callback,String debug){
        if (null==customId||customId.length()<=0){
            Logger.W("Fail join custom group while custom group id invalid "+debug);
            return notifySendFinish(false, "Group custom id invalid", null, callback)&&false;
        }
        Logger.D("Join custom group "+customId+" "+groupType);
        return sendText(Operation.JOIN_GROUP,new Json().putSafe(Label.LABEL_CUSTOM_ID,customId).putSafe(Label.LABEL_GROUP_NAME,groupName).
                putSafe(Label.LABEL_GROUP_TYPE, groupType), (boolean succeed, String note, Frame frame) ->{
            Reply reply=succeed&&null!=frame?frame.getBodyReply():null;
            notifySendFinish(null!=reply&&reply.isSucceed(),note,reply,callback);
        },debug);
    }

    public final boolean agreeInviteCreateGroup(String actionToken,String groupType,OnSendFinish callback,String debug){
        if (null==groupType||groupType.length()<=0){
            Logger.W("Fail agree invite create group while group type invalid.");
            return notifySendFinish(false,"Group type invalid.",null,callback)&&false;
        }
        Logger.M("Agree invite create group.","Agree invite create group "+actionToken+" "+groupType);
        return sendText(Operation.AGREE_INVITE_CREATE_GROUP, new Json().putSafe(Label.LABEL_ACTION_TOKEN, actionToken).
                putSafe(Label.LABEL_GROUP_TYPE, groupType), (boolean succeed, String note, Frame frame)-> {
                    Reply reply=succeed&&null!=frame?frame.getBodyReply():null;
                    notifySendFinish(null!=reply&&reply.isSucceed(),note,reply,callback); }, debug);
    }

    public final boolean inviteCreateGroup(String groupType, String toUid, OnSendFinish callback, String debug){
        if (null==groupType||groupType.length()<=0){
            Logger.W("Fail invite create group while group type invalid.");
            return notifySendFinish(false,"Group id invalid.",null,callback)&&false;
        }
        if (null==toUid||toUid.length()<=0){
            Logger.W("Fail invite create group while to uid invalid.");
            return notifySendFinish(false,"Uid invalid.",null,callback)&&false;
        }
        return sendText(Operation.INVITE_CREATE_GROUP, new Json().putSafe(Label.LABEL_GROUP_TYPE, groupType).
                putSafe(Label.LABEL_TO_UID, toUid),(boolean succeed, String note, Frame frame)-> {
            Reply reply=succeed&&null!=frame?frame.getBodyReply():null;
            notifySendFinish(null!=reply&&reply.isSucceed(),note,reply,callback);
            }, debug);
    }

    public final boolean agreeJoinGroupApply(String actionToken,OnSendFinish callback,String debug){
        if (null==actionToken||actionToken.length()<=0){
            Logger.W("Fail agree join group apply while token invalid "+debug);
            return notifySendFinish(false,"Token invalid.",null,callback)&&false;
        }
        return sendText(Operation.AGREE_JOIN_GROUP_APPLY,new Json().putSafe(Label.LABEL_ACTION_TOKEN,actionToken),
                (boolean succeed, String note, Frame frame)-> {
            Reply reply=succeed&&null!=frame?frame.getBodyReply():null;
            notifySendFinish(null!=reply&&reply.isSucceed(),note,reply,callback);
        },debug);
    }

}
