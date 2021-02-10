package com.csdk.api.audio;
/**
 * Create LuckMerlin
 * Date 18:12 2021/1/27
 * TODO
 */
public final class VoiceFilePlaying {

    private final OnAudioPlayFinish mCallback;
    private final AudioObject mAudioObject;

    public  VoiceFilePlaying(AudioObject audioObject, OnAudioPlayFinish callback){
        mAudioObject=audioObject;
        mCallback=callback;
    }

    public OnAudioPlayFinish getCallback() {
        return mCallback;
    }

    public AudioObject getAudioObject() {
        return mAudioObject;
    }

    public final String getFilePath(){
        AudioObject audioObject=mAudioObject;
        return null!=audioObject?audioObject.getAudioPath():null;
    }
}
