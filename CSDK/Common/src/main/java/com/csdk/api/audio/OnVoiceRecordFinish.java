package com.csdk.api.audio;

/**
 * Create LuckMerlin
 * Date 14:48 2020/10/14
 * TODO
 */
public interface OnVoiceRecordFinish {
    int RECORD_SUCCEED=-4653;
    int RECORD_FAIL=-4654;
    int RECORD_NONE_PERMISSION=-4655;
    void onVoiceRecordFinish(int finish, long duration, String recordFile, String translate);
}
