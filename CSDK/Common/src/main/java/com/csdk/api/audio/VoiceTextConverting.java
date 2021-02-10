package com.csdk.api.audio;

/**
 * Create LuckMerlin
 * Date 20:26 2021/1/27
 * TODO
 */
public final class VoiceTextConverting {

    private final OnTextConvertFinish mCallback;
    private final String mVoiceFile;

    public VoiceTextConverting(String voiceFile,OnTextConvertFinish callback){
        mVoiceFile=voiceFile;
        mCallback=callback;
    }

    public OnTextConvertFinish getCallback() {
        return mCallback;
    }

    public String getVoiceFile() {
        return mVoiceFile;
    }
}
