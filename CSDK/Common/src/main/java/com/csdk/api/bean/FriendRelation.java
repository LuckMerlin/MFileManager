package com.csdk.api.bean;

public interface FriendRelation {
    int NONE=0x0000;
    int ADD_FRIEND=1; //0000 0010
    int ACCEPT_ADD_FRIEND=2;//0000 0100
    int DELETE_FRIEND=4;//0000 1000
    int BLOCK_FRIEND=8;// 0001 0000
    int ALL=ADD_FRIEND|ACCEPT_ADD_FRIEND|DELETE_FRIEND|BLOCK_FRIEND;//0x 0000 0001
}
