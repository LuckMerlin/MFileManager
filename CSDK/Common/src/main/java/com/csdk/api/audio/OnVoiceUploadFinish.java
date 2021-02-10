package com.csdk.api.audio;

/**
 * Create LuckMerlin
 * Date 19:46 2020/10/15
 * TODO
 */
public interface OnVoiceUploadFinish {
    void onVoiceUploadFinish(boolean succeed, String note, String localPath, String cloudPath);
}
