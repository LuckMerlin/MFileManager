package com.csdk.api.audio;

/**
 * Create LuckMerlin
 * Date 11:32 2020/10/16
 * TODO
 */
public interface OnAudioPlayFinish {
    void onAudioPlayFinish(boolean succeed, String note, String filePath);
}
