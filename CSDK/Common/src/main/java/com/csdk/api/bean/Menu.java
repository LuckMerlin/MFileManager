package com.csdk.api.bean;

import com.csdk.api.core.GroupType;
import com.csdk.server.data.Bool;

/**
 * Create LuckMerlin
 * Date 14:00 2020/8/21
 * TODO
 */
public final class Menu<T extends Group> implements Channel, GroupType {
    public final static String MENU_TYPE_MENU="menu";
    public final static String MENU_TYPE_CHANNEL="channel";
    private String id;
    private String createTime;
    private int createUid;
    private String productId;
    private String menuType;
    private String menuKey;
    private String title;
    private String subTitle;
    private int visible;
    private int sort;
    private T group;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public final String getChannelKey(){
        String menuType=this.menuType;
        return null!=menuType&&menuType.equals(GROUP_TYPE_CHANNEL)?menuKey:null;
    }

    public final boolean isMenuKey(String key){
        String curr=this.menuKey;
        return (null==curr&&null==key)||(null!=curr&&null!=key&&curr.equals(key));
    }

    public final boolean isChannelIdMatch(String id){
        String currentId=this.id;
        return null!=id&&null!=currentId&&id.equals(currentId);
    }

    @Override
    public final String getTitle(boolean menuFormat) {
        T group=this.group;
        String innerTitle=null!=group?group.getTitle():null;
        String titleValue= null!=innerTitle&&innerTitle.length()>0?innerTitle:title;
        int length=menuFormat&&null!=titleValue?titleValue.length():-1;
        if (length>0){
            StringBuffer buffer=new StringBuffer(length*2);
            for (int i = 0; i < length; i++) {
                if (i>0&&i<length-1){
                    buffer.append("\n");
                }
                if (i>=5){
                    buffer.append("...");
                    break;
                }
                buffer.append(titleValue.charAt(i));
            }
           titleValue=buffer.toString();
        }
        return titleValue;
    }

    public final boolean isMenuKeyMatched(String key){
        String currentKey=menuKey;
        return null!=key&&null!=currentKey&&currentKey.equals(key);
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getCreateUid() {
        return createUid;
    }

    public void setCreateUid(int createUid) {
        this.createUid = createUid;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getMenuType() {
        return menuType;
    }

    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }

    public String getMenuKey() {
        return menuKey;
    }

    public void setMenuKey(String menuKey) {
        this.menuKey = menuKey;
    }

    public String getSubTitle() {
        return getSubTitle(null);
    }

    public String getSubTitle(Boolean superCase) {
        if (null==superCase){
            return subTitle;
        }
        String title=subTitle;
        title =null!=title?superCase?title.toUpperCase():title.toLowerCase():null;
        int length=null!=title?title.length():-1;
        if (length>0){
            StringBuffer buffer=new StringBuffer(length);
            for (int i = 0; i < length; i++) {
                buffer.append(title.charAt(i));
                buffer.append("\n");
            }
            return buffer.toString();
        }
        return title;
    }

    public void setGroup(T group) {
        this.group = group;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public int getSort() {
        return sort;
    }

    public final boolean isVisible(){
        return Bool.isYes(visible);
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public final String getGroupId() {
        return null!=group?group.getId():null;
    }

    public final T getGroup() {
        return group;
    }
}
