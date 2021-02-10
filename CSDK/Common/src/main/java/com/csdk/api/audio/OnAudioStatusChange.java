package com.csdk.api.audio;

/**
 * Create LuckMerlin
 * Date 11:15 2020/10/23
 * TODO
 */
public interface OnAudioStatusChange {
    int AUDIO_PLAY=-1231;
    int AUDIO_STOP=-1232;
    void onAudioStatusChange(int what, String note, AudioObject audioObject);
}
