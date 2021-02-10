package com.csdk.api.audio;

/**
 * Create LuckMerlin
 * Date 17:52 2021/1/27
 * TODO
 */
public final class VoiceFileUploading {
    private final String mFilePath;
    private final OnVoiceUploadFinish mCallback;

    public VoiceFileUploading(String filePath,OnVoiceUploadFinish callback){
        mFilePath=filePath;
        mCallback=callback;
    }

    public OnVoiceUploadFinish getCallback() {
        return mCallback;
    }

    public String getFilePath() {
        return mFilePath;
    }
}
