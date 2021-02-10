package com.csdk.api.audio;

/**
 * Create LuckMerlin
 * Date 17:40 2021/1/27
 * TODO
 */
public final class VoiceStreamRecoding {
    private long mStartTime;
    private final OnVoiceRecordFinish mCallback;
    private final String mVoiceFile;

    public VoiceStreamRecoding(String voiceFile,OnVoiceRecordFinish callback){
        mStartTime=System.currentTimeMillis();
        mVoiceFile=voiceFile;
        mCallback=callback;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public OnVoiceRecordFinish getCallback() {
        return mCallback;
    }

    public String getVoiceFile() {
        return mVoiceFile;
    }
}
