package com.csdk.api.audio;

/**
 * Create LuckMerlin
 * Date 14:29 2021/1/27
 * TODO
 */
public class VoiceRecording {
    private final OnVoiceRecordFinish mCallback;
    private final long mStartTime;

    public VoiceRecording(OnVoiceRecordFinish callback){
        mStartTime=System.currentTimeMillis();
        mCallback=callback;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public OnVoiceRecordFinish getCallback() {
        return mCallback;
    }
}
