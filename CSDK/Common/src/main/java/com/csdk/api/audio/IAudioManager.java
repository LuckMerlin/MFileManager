package com.csdk.api.audio;

import android.content.Context;

/**
 * Create LuckMerlin
 * Date 11:35 2021/1/26
 * TODO
 */
public interface IAudioManager {
    public boolean stopPlayVoiceFile(String debug);

    public boolean playOrStop(Context context, AudioObject audioObject, String debug);

    public boolean putListener(OnAudioStatusChange callback, String debug);

    public boolean init(Context context,String debug);

    public AudioObject getPlaying();

    public boolean removeListener(OnAudioStatusChange callback, String debug);

    public boolean playVoiceFile(Context context, AudioObject voiceFilePath, OnAudioPlayFinish callback, String debug);

    public boolean stopRecordVoice(String debug);

    public boolean cancelRecordVoice(String debug);

    public boolean startVoiceToText(Context context, String voiceFilePath, OnTextConvertFinish callback, String debug);

    public boolean startRecordVoice(Context context, String recordPath, OnVoiceRecordFinish callback, String debug);

    public boolean enableSpeaker(boolean enable,String debug);

    public boolean enableMicrophone(boolean enable,String debug);

    public boolean isSpeakerEnabled();

    public boolean isMicrophoneEnabled();

    public boolean entryRoom(Context context,int uid,int roomId,String debug);

    public boolean poll(String debug);

    public boolean isInitialed();

    public boolean updateSelfPosition(Coordinate coordinate, String debug);

    public boolean setAudioReceiveRange(int range,String debug);

    public boolean exitRoom(String debug);

    public boolean deInitial(String debug);

    public boolean resetOpenId(String openId,String debug);

    public boolean startUploadVoiceFile(Context context, String voiceFilePath, String cloudPath, OnVoiceUploadFinish callback, String debug);

    public boolean startTranslateText(Context context, String text, Object srcLanguage, Object targetLanguage, OnTextTranslateFinish callback, String debug);
}
