package com.csdk.api.bean;

import com.csdk.api.core.ContentType;
import com.csdk.api.struct.StructArray;
import com.csdk.debug.Logger;
import com.csdk.server.MessageObject;
import com.csdk.api.core.MessageType;
import com.csdk.api.core.Operation;
import com.csdk.api.core.Status;
import com.csdk.api.audio.AudioObject;
import com.csdk.server.cache.Cacheable;
import com.csdk.server.data.Json;
import com.csdk.api.core.Label;
import org.json.JSONArray;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Create LuckMerlin
 * Date 20:51 2020/8/21
 * TODO
 */
public final class Message implements MessageType, ContentType, Serializable, MessageObject, Cacheable, AudioObject {
    private static final long serialVersionUID = -7649307280683632300L;
    private String id;
    private String msgId;
    private String fromUid;
    private String groupId;
    private String groupType;
    private String content;
    private String contentType;
    private int msgType;
    private String time;
    private Map<String, Object> extra;
    private int notifyType;
    private String actToken;
    private List<String> toUid;
    private String loginUid;
    private String mUserRoleName;
    private Integer operation= Operation.NONE;
    private int status= Status.STATUS_NONE;
    private String uversion;
    private String mFromRoleId;
    private String mUserLevel;
    private boolean mUserFemale;
    private String mUserAvatarUrl;
    private int mSendRetry;
    private String version;

    public Message(Integer operation){
        this(operation,null);
    }

    public Message(Integer operation, String content, String ...uid){
        this(operation,content, CONTENTTYPE_TEXT,uid);
    }

    public Message(Integer operation, String content, String contentType, String ...uid){
        this(operation,null!=uid&&uid.length>0? Arrays.asList(uid):null,content,contentType,null,null);
    }

    public Message(Integer operation, List<String> toUid, String content, String contentType, String groupId, String groupType){
        this(operation,null,toUid,content,contentType,groupId,groupType,null,null);
    }

    protected Message(Integer operation, String fromUid, List<String> toUid, String content, String contentType,
                      String groupId, String groupType, String msgId, String time){
        this.fromUid=fromUid;
        this.operation=operation;
        this.toUid=toUid;
        this.content=content;
        this.contentType=contentType;
        this.groupId=groupId;
        this.groupType=groupType;
        this.msgId=null!=msgId?msgId:"android"+(Math.random()*System.currentTimeMillis())+ UUID.randomUUID();
        this.time=time;
    }

    public Message setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getFromUserRoleName() {
        String extra=getExtraString(Label.LABEL_USER_NAME);
        return null!=extra&&extra.length()>0?extra:mUserRoleName;
    }

    public final StructArray getStruct(){
        String structJsonText=isContentType(ContentType.CONTENTTYPE_STRUCT)?content:null;
        return null!=structJsonText&&structJsonText.length()>0?new StructArray(Json.createArray(structJsonText)):null;
    }

    public void setFromUserRoleName(String userRoleName) {
        this.mUserRoleName = userRoleName;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setLoginUid(String loginUid) {
        this.loginUid = loginUid;
    }

    public final String getLoginUid() {
        return loginUid;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public final int getMsgType() {
        return msgType;
    }

    public String getFromUid() {
        return fromUid;
    }

    public void setFromUid(String fromUid) {
        this.fromUid = fromUid;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public final boolean isVoiceMessage(){
        String contentType=getContentType();
        return null!=contentType&&contentType.equals(CONTENTTYPE_VOICE);
    }

    public boolean isSelfMessage(){
        String uid=loginUid;
        String fromUid=getFromUid();
        return null==fromUid||fromUid.length()<=0||(null!=uid&&fromUid.equals(uid));
    }

    public final boolean isFromUid(String uid){
        String fromUid=getFromUid();
        return null!=uid&&null!=fromUid&&uid.equals(fromUid);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getTime() {
        try {
            return null!=time&&time.length()>0?Long.parseLong(time):0;
        }catch (Exception e){

        }
        return  0;
    }

    public String getVoiceTranslation() {
        return getExtraString(Label.LABEL_TRANSLATION);
    }

    public Message setVoiceTranslation(String translation) {
        putExtra(Label.LABEL_TRANSLATION,translation);
        return this;
    }

    public Message setFromRoleId(String fromRoleId) {
        this.mFromRoleId = fromRoleId;
        return this;
    }

    public String getFromRoleId() {
        return mFromRoleId;
    }

    public boolean isUserFemale() {
        return mUserFemale;
    }

    public String getUserLevel() {
        return mUserLevel;
    }

    public Message setOperation(Integer operation) {
        this.operation = operation;
        return this;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public final Integer getOperation() {
        return operation;
    }

    @Override
    public final String getMessageText() {
        Map<String, Object> extra=getExtra();
        if (null!=extra){//Check translate if EMPTY for IOS client parse
            Object translateObject=extra.get(Label.LABEL_TRANSLATION);
            String translate=null!=translateObject?translateObject.toString():null;
            if (null!=translate&&translate.length()<=0){
                extra.remove(Label.LABEL_TRANSLATION);//Remove translate while empty
            }
        }
        return new Json().putSafe("toUid",toJsonArray(getToUid())).putSafe("content",getContent()).
                putSafe("contentType",getContentType()).putSafe("version",version).putSafe("groupId",getGroupId()).putSafe("groupType",
                getGroupType()).putSafe("msgType",msgType).putSafe("msgId",getMsgId()).putSafe("extra",
                new Json().put(extra)).toString();
    }

    public String getVersion() {
        return version;
    }

    @Override
    public final String getAudioPath() {
        String contentType=getContentType();
        return null!=contentType&&contentType.equals(CONTENTTYPE_VOICE)?getContent():null;
    }

    public String getUserVersion() {
        return uversion;
    }

    public int getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(int notifyType) {
        this.notifyType = notifyType;
    }

    public String getActionToken() {
        return actToken;
    }

    public final Map<String, Object> getExtra() {
        return extra;
    }

    public final String getExtraString(String key) {
        Object extraObj=getExtra(key);
        return null!=extraObj?extraObj.toString():null;
    }

    public final Object getExtra(String key) {
        Map<String, Object> extra=this.extra;
        return null!=key&&null!=extra?extra.get(key):null;
    }

    public final boolean isExistExtra(String key){
        Map<String, Object> extra=this.extra;
        return null!=extra&&null!=key&&extra.containsKey(key);
    }

    public final boolean isContentType(String ...contentTypes){
        String current=this.contentType;
        if (null!=contentTypes&&contentTypes.length>0){
            for (String child:contentTypes) {
                if (null!=child&&child.equals(current)){
                    return true;
                }
            }
        }
        return false;
    }

    public final boolean isGroupType(String groupType){
        String current=this.groupType;
        return null!=groupType&&null!=current&&current.equals(groupType);
    }


    public final boolean putExtra(Map<String, Object> extra){
        Set<String> set=null!=extra&&extra.size()>0?extra.keySet():null;
        if (null!=set){
            for (String child:set) {
                putExtra(child, extra.get(child));
            }
            return true;
        }
        return false;
    }

    public final Object putExtra(String key,Object value){
        if (null!=key){
            Map<String, Object> extra=this.extra;
            extra=null!=extra?extra:(this.extra=new HashMap<>());
            if (null==value){
                return extra.remove(key);
            }
            return extra.put(key, value);
        }
        return null;
    }

    public String getUserAvatarUrl() {
        return mUserAvatarUrl;
    }

    public void setUserFemale(boolean userFemale) {
        this.mUserFemale = userFemale;
    }

    public void setUserLevel(String userLevel) {
        this.mUserLevel = userLevel;
    }

    public void setUserAvatarUrl(String userAvatarUrl) {
        this.mUserAvatarUrl = userAvatarUrl;
    }

    @Override
    public boolean equals( Object obj) {
        if (null!=obj){
            if (obj instanceof Message){
                obj=((Message)obj).msgId;
            }
            if(obj instanceof String){
                String msgId=this.msgId;
                return null!=msgId&&msgId.equals(obj);
            }
        }
        return super.equals(obj);
    }

    public String getFirstToUid() {
        List<String>  current= toUid;
        return null!=current&&current.size()>0?current.get(0):null;
    }

    public List<String> getToUid() {
        return toUid;
    }

    public final boolean append2Uids(String ...uids) {
        if (null!=uids&&uids.length>0){
            List<String>  current= toUid;
            current=null!=current?current:(toUid=new ArrayList<String>());
            for (String child:uids) {
                if (!current.contains(child)){
                    current.add(child);
                }
            }
            return true;
        }
        return false;
    }

    public final String getSessionTargetUniqueId(){
        switch (msgType){
            case MESSAGETYPE_GROUP:
                String groupType=this.groupType;
                String groupId=this.groupId;
                return null!=groupType&&null!=groupId&&groupType.length()>0&&
                        groupId.length()>0?"|||Group||Unique|||"+groupType+groupId:null;
            case MESSAGETYPE_SINGLE:
                String toUid=getFirstToUid();
                return null!=toUid&&toUid.length()>0?"|||Single||Unique|||"+toUid:null;
        }
        return null;
    }

    public void setToUid(List<String> toUid) {
        this.toUid = toUid;
    }

    protected final JSONArray toJsonArray(Collection<?> collection){
        if (null!=collection&&collection.size()>0){
            JSONArray jsonArray=new JSONArray();
            for (Object uid:collection) {
                if (null!=uid){
                    jsonArray.put(uid);
                }
            }
            return jsonArray;
        }
        return null;
    }

    public void setSendTry(int retry) {
        this.mSendRetry = retry;
    }

    public int getSendTry() {
        return mSendRetry;
    }

    public final Link getLink(){
        Object linkJsonObject=getExtra(Label.LABEL_LINK);
        String linkJsonText=null!=linkJsonObject?linkJsonObject.toString():null;
        Json linkJson=null!=linkJsonText&&linkJsonText.length()>0?Json.create(linkJsonText):null;
        return null!=linkJson?new Link(linkJson):null;
    }

    public final Refer getRefer(){
        Object REFERJsonObject=getExtra(Label.LABEL_REFER);
        String referJsonText=null!=REFERJsonObject?REFERJsonObject.toString():null;
        Json linkJson=null!=referJsonText&&referJsonText.length()>0?Json.create(referJsonText):null;
        return null!=linkJson?new Refer(linkJson):null;
    }

    public final boolean isLinkMessage(){
        return null!=getLink();
    }

    protected final byte[] generateBytes(Json json, String charset){
        String text=null!=json?json.toString():null;
        return null!=text?generateBytes(text, charset):null;
    }

    protected final byte[] generateBytes(String text,String charset){
        try {
            return null!=text?(null!=charset&&charset.length()>0?text.getBytes(charset):text.getBytes()):null;
        } catch (UnsupportedEncodingException e) {
            Logger.E("Can't generate transport bytes "+e+" "+charset);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "Message{" +
                "msgId='" + msgId + '\'' +
                ", fromUid='" + fromUid + '\'' +
                ", groupId='" + groupId + '\'' +
                ", groupType='" + groupType + '\'' +
                ", content='" + content + '\'' +
                ", contentType='" + contentType + '\'' +
                ", time=" + time +
                ", toUid=" + toUid +
                ", status=" + status +
                '}';
    }

}
