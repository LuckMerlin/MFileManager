package com.csdk.api.audio;

/**
 * Create LuckMerlin
 * Date 11:51 2021/1/26
 * TODO
 */
public final class RoomUser {

    private final int mUserId;
    private Integer mRoomId;

    public RoomUser(int userId, Integer roomId){
        mUserId=userId;
        mRoomId=roomId;
    }

    public void setRoomId(Integer roomId) {
        this.mRoomId = roomId;
    }

    public boolean isUserIdEquals(int userId){
        return mUserId==userId;
    }

    public boolean isRoomIdEquals(int roomId){
        Integer current=mRoomId;
        return null!=current&&current==roomId;
    }

    public Integer getRoomId() {
        return mRoomId;
    }

}
