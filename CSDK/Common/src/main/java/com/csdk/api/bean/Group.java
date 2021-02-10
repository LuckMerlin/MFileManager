package com.csdk.api.bean;

import com.csdk.api.core.MessageType;

/**
 * Create LuckMerlin
 * Date 17:10 2020/8/20
 * TODO
 */
public final class Group implements Session {
    private String id;
    private String createTime;
    private String productId;
    private String serverId;
    private String creator;
    private String customId;
    private String iconUrl;
    private String owner;
    private String type;
    private String title;
    private int maxNum;

    public Group(){
        this(null,null);
    }

    public Group(String id, String type){
        this.id=id;
        this.type=type;
    }

    @Override
    public final String getLogo() {
        return iconUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getId() {
        return id;
    }

    public Integer getIdInteger(Integer def) {
        String id=this.id;
        if (null!=id&&id.length()>0){
            try {
                return Integer.parseInt(id);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return def;
    }

    public String getCustomId() {
        return customId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

//    public int getMessageSendFrequencyLimit() {
//        return msgFreq;
//    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getOwner() {
        return owner;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public final String getGroupUnique(){
         String id=this.id;
         String type=this.type;
         return null!=id&&null!=type?id+"_"+type:null;
    }

    public final boolean hasJoinGroup(){
        String groupId=this.id;
        return null!=groupId&&!groupId.equalsIgnoreCase("0");
    }

    public final boolean applyForSendMessage(Message message){
        if (null!=message){
            message.setGroupType(getType());
            message.setGroupId(getId());
            message.setMsgType(MessageType.MESSAGETYPE_GROUP);
            message.setToUid(message.getToUid());
            return true;
        }
        return false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    @Override
    public String toString() {
        return "Group{ title=" + title+"}";
    }
}
