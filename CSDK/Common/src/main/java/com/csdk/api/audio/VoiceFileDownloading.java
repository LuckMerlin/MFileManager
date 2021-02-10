package com.csdk.api.audio;

/**
 * Create LuckMerlin
 * Date 18:42 2021/1/27
 * TODO
 */
public final class VoiceFileDownloading {

    private final String mFilePath;
    private final OnVoiceDownloadFinish mCallback;

    public  VoiceFileDownloading(String filePath,OnVoiceDownloadFinish callback){
        mFilePath=filePath;
        mCallback=callback;
    }

    public OnVoiceDownloadFinish getCallback() {
        return mCallback;
    }

    public String getFilePath() {
        return mFilePath;
    }
}
