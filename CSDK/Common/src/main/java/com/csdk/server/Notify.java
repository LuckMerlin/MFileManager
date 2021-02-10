package com.csdk.server;

/**
 * Create LuckMerlin
 * Date 20:37 2020/8/6
 * TODO
 */
public interface Notify {
//    NotifyTypeTips                 = 0 // 普通系统消息
//    NotifyTypeAddFriend            = 1 // 申请添加好友
//    NotifyTypeAddFriendAgree       = 2 // 同意添加好友
//    NotifyTypeJoinGroup            = 3 // 申请加群
//    NotifyTypeJoinGroupAgree       = 4 // 同意加群
//    NotifyTypeInviteJoinGroup      = 5 // 邀请加群
//    NotifyTypeInviteJoinGroupAgree = 6 // 同意邀请加群
//    NotifyFriendOnline             = 7 // 好友上线
//    0: 普通系统消息,1: 申请添加好友,2: 同意添加好友,3: 申请加群,4: 同意加群
    int NOTIFY_INVALID=-1;
    int NOTIFY_SYSTEM_NORMAL=0;
    int NOTIFY_ADD_FRIEND=1;
    int NOTIFY_ACCEPT_ADD_FRIEND=2;
    int NOTIFY_REQUEST_ADD_GROUP=3;
    int NOTIFY_JOIN_GROUP_REPLY_ACCEPTED=4;
    int NOTIFY_INVITE_JOIN_GROUP=5;
    int NOTIFY_AGREE_JOIN_GROUP=6;//同意邀请加群
    int NOTIFY_FRIEND_ONLINE=7;// 好友上线下线
    int NOTIFY_GROUP_HAS_QUIT=8;//  群已经被解散
    int NOTIFY_GROUP_QUITED=9;//  有人退出群
    /**
     * @deprecated
     */
    int NOTIFY_INVITE_JOIN_TEAM=10; //Invite join team

    int NOTIFY_GROUP_JOINED=11; //加入群组
    int NOTIFY_SHIENDSHIP_CHANGE=12; //好友关系链变化
    int NOTIFY_ACCEPT_JOIN_INTO_GROUP=13; //同意加入群组
    int NOTIFY_NONE_SPEAKING=100; //被禁言

    int NOTIFY_INVITE_JOIN_INTO_GROUP=NOTIFY_INVITE_JOIN_TEAM;

}
