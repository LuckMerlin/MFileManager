package com.csdk.api.core;

/**
 * Create LuckMerlin
 * Date 14:16 2020/8/13
 * TODO
 */
public interface Operation {
    public final static int NONE=-1;//None
    public final static int HANDSHAKE=0;//Handshake
    public final static int HEARTBEAT=2;//Heartbeat
    public final static int HEARTBEAT_REPLY=3;//Heartbeat reply
    public final static int SEND=4;
    public final static int SYSTEM_MESSAGE=1000;
    public final static int SEND_RECEIPT=5;
    public final static int AUTH=7;
    public final static int AUTH_REPLY=8; //Auth reply
    public final static int MESSAGE_RECEIVE=9; //IM message
    public final static int JOIN_ROOM=18;
    public final static int JOIN_ROOM_REPLY=19;
    public final static int LEAVE_ROOM=20;
    public final static int LEAVE_ROOM_REPLY=21;
    public final static int ADD_FRIEND=22;//Add friend
    public final static int ADD_FRIEND_REPLY=23;//Add friend reply
    public final static int ADD_FRIEND_AGREE=24;//Add friend agree
    public final static int ADD_FRIEND_AGREE_REPLY=25;//Add friend agree reply
    public final static int BLOCK_FRIEND=26;//Block friend
    public final static int BLOCK_FRIEND_REPLY=27;//Block friend reply
    public final static int UNBLOCK_FRIEND=28;//Unblock friend
    public final static int UNBLOCK_FRIEND_REPLY=29;//Unblock friend reply
    public final static int CREATE_GROUP=30;//Create group
    public final static int CREATE_GROUP_REPLY=31;//Create group reply
    public final static int JOIN_GROUP=32;//Join group
    public final static int JOIN_GROUP_REPLY=33;//Join group reply
    public final static int JOIN_GROUP_AGREE=34;//Join group agree
    public final static int JOIN_GROUP_AGREE_REPLY=35;//Join group agree reply
    public final static int INVITE_JOIN_GROUP=36;//Invite join group
    public final static int INVITE_JOIN_GROUP_REPLY=37;//Join group reply
    public final static int INVITE_JOIN_GROUP_AGREE=38;
    public final static int INVITE_JOIN_GROUP_AGREE_REPLY=39;
    public final static int QUIT_GROUP=40;
    public final static int QUIT_GROUP_REPLY=41;
    public final static int DISMISS_GROUP=42;//
    public final static int DISMISS_GROUP_REPLY=43;//
    /**
     * @deprecated
     */
    public final static int INVITE_JOIN_TEAM=44;
    public final static int APPLY_JOIN_GROUP=INVITE_JOIN_TEAM;

    public final static int INVITE_JOIN_TEAM_REPLY=45;
    /**
     * @deprecated
     */
    public final static int APPLY_JOIN_GROUP_REPLY=INVITE_JOIN_TEAM_REPLY;
    /**
     * @deprecated
     */
    public final static int INVITE_JOIN_TEAM_AGREE=46;
    /**
     * @deprecated
     */
    public final static int INVITE_JOIN_TEAM_AGREE_REPLY=47;

    public final static int INVITE_CREATE_GROUP=48;//邀请创建队伍
    public final static int INVITE_CREATE_GROUP_REPLY=49;
    public final static int AGREE_INVITE_CREATE_GROUP=50;//同意邀请创建频道
    public final static int AGREE_INVITE_CREATE_GROUP_REPLY=51;

    public final static int QUIT_TEAM=52;
    public final static int QUIT_TEAM_REPLY=53;
    public final static int DELETE_FRIEND=54;
    public final static int DELETE_FRIEND_REPLY=55;
    public final static int NOT_FOUND=404;
    public final static int SYSTEM_FORCE_EXIT=1001;
    public final static int AGREE_JOIN_GROUP_APPLY=INVITE_JOIN_TEAM_AGREE;
    public final static int AGREE_JOIN_GROUP_APPLY_REPLY=INVITE_JOIN_TEAM_AGREE_REPLY;
//    OpGroupJoinApply      = int32(44)
//    OpGroupJoinApplyReply = int32(45)
//    // 同意加群申请
//    OpGroupJoinApplyAgree      = int32(46)
//    OpGroupJoinApplyAgreeReply = int32(47)
}
