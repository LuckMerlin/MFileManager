package com.csdk.api.bean;

import com.csdk.debug.Logger;
import com.csdk.server.data.Bool;
import com.csdk.api.core.Label;
import com.csdk.server.util.TimeOutline;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Create LuckMerlin
 * Date 15:52 2020/8/21
 * TODO
 */
public class User implements Serializable, Session {
    private String id;
    private String createTime;
    private String productId;
    private String serverId;
    private String roleId;
    private String roleName;
    private String nickName;
    private String avatarUrl;
    private String version;
    /**
     * 1 Man
     * 2 Female
     */
    private int gender;
//    private long ip;
    private int blocked;
    private int isFriend;
    private int isOnline;
    private Third third;
    private int mutualFriends;
    private Object team;
    private Map<String, String> onlineStatus;
    private String cityName;
    private String slogan;
    private String areaName;
    private Profile profile;
    private String distance;
    private List<Image> assets;

    public User(String id){
        this(id,null);
    }

    public User(String id, String nickName){
        this.nickName=nickName;
        this.id=id;
    }

    public final boolean isUidMatch(String ...uids){
        String uid=this.id;
        if (null!=uids&&null!=uid&&uids.length>0){
            for (String child:uids ) {
                if (null!=child&&child.equals(uid)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getLogo() {
        return avatarUrl;
    }

    @Override
    public String getTitle() {
        return roleName;
    }

    public List<Image> getAssets() {
        return assets;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getId() {
        return id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setGamePreferMain(String gamePreferMain) {
        Profile profile=this.profile;
        profile=null!=profile?profile:(this.profile=new Profile());
        Profile.ExtraBean extra=profile.extra;
        extra=null!=extra?extra:(profile.extra=new Profile.ExtraBean());
        extra.perfer1 = gamePreferMain;
    }

    public void setGamePreferSub(String gamePreferSub) {
        Profile profile=this.profile;
        profile=null!=profile?profile:(this.profile=new Profile());
        Profile.ExtraBean extra=profile.extra;
        extra=null!=extra?extra:(profile.extra=new Profile.ExtraBean());
        extra.perfer2 = gamePreferSub;
    }

    public String getGamePreferMain() {
        Profile profile=this.profile;
        Profile.ExtraBean extra =null!=profile?profile.extra:null;
        return null!=extra?extra.perfer1:null;
    }

    public String getGamePreferSub() {
        Profile profile=this.profile;
        Profile.ExtraBean extra =null!=profile?profile.extra:null;
        return null!=extra?extra.perfer2:null;
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

    public String getRoleId() {
        return roleId;
    }

    public User setRoleId(String roleId) {
        this.roleId = roleId;
        return this;
    }

    public String getRoleName() {
        return roleName;
    }

    public User setRoleName(String roleName) {
        this.roleName = roleName;
        return this;
    }

    public String getNickName() {
        return nickName;
    }

    public User setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /**
     * @deprecated
     */
    public final boolean isFriendBlocked(){
        return Bool.isYes(blocked);
    }

    public final boolean isBlocked(){
        return isFriendBlocked();
    }

    public boolean isFriend() {
        return Bool.isYes(isFriend);
    }

    public Third getThird() {
        return third;
    }

    public boolean setFriend(boolean friend){
        this.isFriend=friend? Bool.YES: Bool.NO;
        return true;
    }

    public boolean setBlocked(boolean blocked){
        this.blocked=blocked? Bool.YES: Bool.NO;
        return true;
    }

    public int getIsOnline() {
        return isOnline;
    }

    public final boolean isOnline() {
        return  Bool.isYes(isOnline);
    }

    public void setOnline(boolean online) {
        this.isOnline = online?Bool.YES: Bool.NO;
    }

    public Object getTeam() {
        return team;
    }

    public void setTeam(Object team) {
        this.team = team;
    }

    public boolean isFemale(){
        return gender== Gender.FEMALE;
    }

    public String getCityName() {
        Profile profile=this.profile;
        return null!=cityName&&cityName.length()>0?cityName:(null!=profile?profile.cityName:null);
    }

    public Profile getProfile() {
        return profile;
    }

    public String getSlogan() {
        return slogan;
    }

    public String getAreaName() {
        Profile profile=this.profile;
        return null!=areaName&&areaName.length()>0?areaName:(null!=profile?profile.areaName:null);
    }

    public String getCityAndArea(){
        String cityName=getCityName();
        String areaName=getAreaName();
        return (null!=cityName?cityName+" ":"")+(null!=areaName?areaName:"");
    }

    public final void setInterest(String interest) {
        Profile profile=this.profile;
        profile=null!=profile?profile:(this.profile=new Profile());
        Profile.ExtraBean extra=profile.extra;
        extra=null!=extra?extra:(profile.extra=new Profile.ExtraBean());
        extra.interest = interest;
    }

    public final void setOnlineDay(String onlineDay) {
        Profile profile=this.profile;
        profile=null!=profile?profile:(this.profile=new Profile());
        Profile.ExtraBean extra=profile.extra;
        extra=null!=extra?extra:(profile.extra=new Profile.ExtraBean());
        extra.onlineDay = onlineDay;
    }

    public final void setOnlineTime(String onlineTime) {
        Profile profile=this.profile;
        profile=null!=profile?profile:(this.profile=new Profile());
        Profile.ExtraBean extra=profile.extra;
        extra=null!=extra?extra:(profile.extra=new Profile.ExtraBean());
        extra.onlineTime = onlineTime;
    }

    public final String getInterest() {
        Profile profile=this.profile;
        Profile.ExtraBean extra =null!=profile?profile.extra:null;
        return null!=extra?extra.interest:null;
    }

    public final Option getInterestOption() {
        String interest=getInterest();
        return null!=interest?new Option(Label.LABEL_INTEREST, interest):null;
    }

    public float getDistance() {
        try {
            return null!=distance&&distance.length()>0?Float.parseFloat(distance):-1;
        }catch (Exception e){
            Logger.E("Exception parser user distance.e="+e,e);
            e.printStackTrace();
        }
        return -1;
    }

    public final String getOnlineDay() {
        Profile profile=this.profile;
        Profile.ExtraBean extra =null!=profile?profile.extra:null;
        return null!=extra?extra.onlineDay:null;
    }

    public final Option getOnlineDayOption() {
        String onlineDay=getOnlineDay();
        return null!=onlineDay?new Option(Label.LABEL_ONLINE_DAY, onlineDay ):null;
    }

    public Option getGamePreferMainOption() {
        String value=getGamePreferMain();
        return null!=value?new Option(Label.LABEL_PREFER_MAIN, value):null;
    }

    public Option getGamePreferSubOption() {
        String value=getGamePreferSub();
        return null!=value?new Option(Label.LABEL_PREFER_SUB, value):null;
    }

    public final String getOnlineTime() {
        Profile profile=this.profile;
        Profile.ExtraBean extra =null!=profile?profile.extra:null;
        return null!=extra?extra.onlineTime:null;
    }

    public final Option getOnlineTimeOption() {
        String onlineTime=getOnlineTime();
        return null!=onlineTime?new Option(Label.LABEL_ONLINE_TIME, onlineTime ):null;
    }

    public final User setUserLevel(String level){
        Profile profile=this.profile;
        profile=null!=profile?profile:(this.profile=new Profile());
        profile.level=level;
        return this;
    }

    public String getUserLevel(){
        Profile profile=this.profile;
        return null!=profile?profile.level:null;
    }

    public int getMutualFriends() {
        return mutualFriends;
    }

    public Map<String, String> getOnlineStatus() {
        return onlineStatus;
    }

    public String getStatusName(){
        Map<String, String> status=onlineStatus;
        Object object= null!=status?status.get(Label.LABEL_STATUS_NAME):null;
        return null!=object&&object instanceof String?((String)object):null;
    }


    public final void setTags(List<Tag> tags) {
        Profile profile=this.profile;
        profile=null!=profile?profile:(this.profile=new Profile());
        List<String> tagsValues=(profile.tags=new ArrayList<>());
        for (Tag child:tags) {
            String title=null!=child?child.getTitle():null;
            if (null!=title&&!tagsValues.contains(title)){
                tagsValues.add(title);
            }
        }
    }

    public final List<String> getTagTexts(){
        Profile profile=this.profile;
        return null!=profile?profile.tags:null;
    }

    public final List<Tag> getTags() {
        Profile profile=this.profile;
        List<String> tags= null!=profile?profile.tags:null;
        List<Tag> list=null;
        if (null!=tags&&tags.size()>0){
            list=new ArrayList<>();
            for (String child:tags) {
                if (null!=child){
                    list.add(new Tag(child));
                }
            }
        }
        return list;
    }

    public String getHeatDegree(){
        return null;
    }

    public String getLastOnlineTimeText(){
        Map<String, String> status=onlineStatus;
        return null!=status?status.get(Label.LABEL_LAST_ONLINE_TIME):null;
    }

    public String getOnlineStatusCode(){
        Map<String, String> status=onlineStatus;
        Object object= null!=status?status.get(Label.LABEL_STATUS):null;
        return null!=object?object.toString():null;
    }

    public long getLastOnlineTime(long def){
        String timeText=getLastOnlineTimeText();
        if (null!=timeText&&timeText.contains("-")&&timeText.contains(":")){
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date=sdf.parse(timeText);
                return date.getTime();
            } catch (ParseException e) {
                //Do nothing
            }
        }
        return def;
    }

    public String getLastOnlineTimeOutline(String def){
        long time=getLastOnlineTime(-1);
        if (time>0){
            return new TimeOutline().outline(time, def);
        }
        return def;
    }

    private static class Profile{
        private double longitude;
        private double latitude;
        private String city;
        private String cityName;
        private String areaName;
        private ExtraBean extra;
        private List<Double> location;
        private List<String> tags;
        private String level;

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public String getAreaName() {
            return areaName;
        }

        public void setAreaName(String areaName) {
            this.areaName = areaName;
        }

        public ExtraBean getExtra() {
            return extra;
        }

        public void setExtra(ExtraBean extra) {
            this.extra = extra;
        }

        public List<Double> getLocation() {
            return location;
        }

        public void setLocation(List<Double> location) {
            this.location = location;
        }

        public String getLevel() {
            return level;
        }

        private static class ExtraBean {
            /**
             * interest : 上分大神
             * onlineDay : 工作日
             * onlineTime : 全天
             * tags : [" 医疗兵","职业树洞"]
             */

            private String interest;
            private String onlineDay;
            private String onlineTime;
            private String perfer1;
            private String perfer2;

            public String getInterest() {
                return interest;
            }

            public void setInterest(String interest) {
                this.interest = interest;
            }

            public String getOnlineDay() {
                return onlineDay;
            }

            public void setOnlineDay(String onlineDay) {
                this.onlineDay = onlineDay;
            }

            public String getOnlineTime() {
                return onlineTime;
            }

            public void setOnlineTime(String onlineTime) {
                this.onlineTime = onlineTime;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (null!=obj){
            String userId=null;
            if (obj instanceof User){
                userId=((User)obj).getId();
            }else if (obj instanceof String){
                userId=(String)obj;
            }
            if (null!=userId){
                return null!=id&&id.equals(userId);
            }
        }
        return super.equals(obj);
    }
}
