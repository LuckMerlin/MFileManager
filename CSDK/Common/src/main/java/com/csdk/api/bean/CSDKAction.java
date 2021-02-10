package com.csdk.api.bean;
/**
 * Create LuckMerlin
 * Date 13:36 2020/10/21
 * TODO
 */
public enum CSDKAction {
    ACTION_FRIEND_ADD(1989),
    ACTION_FRIEND_DELETE(1990),
    ACTION_FRIEND_BLOCK(1991),
    ACTION_FRIEND_UNBLOCK(1992),
    ACTION_FRIEND_ADD_REQUEST_CHANGE(1993),
    ACTION_FRIEND_GIVE_COIN(1994),
    ACTION_CLICK_LINK(1995),
    ACTION_SHOW_PERSONAL_PAGE(1996),
    //
    ACTION_FRIEND_INVITATION(1997),//邀请好友
    ACTION_GROUP_APPLY(1998),//群组申请
    ACTION_GROUP_INVITATION(2000),//群组邀请
    ACTION_INVITATION(2002),//邀请
    ACTION_GROUP_QUITE(2003),//退出群组
    ACTION_GROUP_JOIN(2004);//已经进入群组

    private int  mAction=0;

    private CSDKAction(int action) {
        mAction=action;
    }

    public int getValue() {
        return mAction;
    }

}
