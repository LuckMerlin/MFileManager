package com.csdk.api.audio;

import android.content.Context;

import com.csdk.api.core.Debug;
import com.csdk.server.Configure;

/**
 * Create LuckMerlin
 * Date 10:52 2021/1/26
 * TODO
 */
public final class AudioManager implements AudioProcessor {
    private static volatile AudioManager mInstance;
    private final IAudioManager mManager=null;

    private AudioManager() {
//        mManager=new GmAudioManager();
    }

    public static AudioManager instance() {
        if (mInstance == null) {
            synchronized (AudioManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioManager();
                }
            }
        }
        return mInstance;
    }

    public final boolean init(Context context,String debug){
        IAudioManager manager=mManager;
        return null!=manager&&manager.init(context,debug);
    }

    public final boolean isInitialed(){
        IAudioManager manager=mManager;
        return null!=manager&&manager.isInitialed();
    }

    public boolean stopPlayVoiceFile(String debug){
        IAudioManager manager=mManager;
        return null!=manager&&manager.stopPlayVoiceFile(debug);
    }

    public final boolean playOrStop(Context context, AudioObject audioObject, String debug){
        IAudioManager manager=mManager;
        return null!=manager&&manager.playOrStop(context, audioObject, debug);
    }

    public boolean putListener(OnAudioStatusChange callback, String debug){
        IAudioManager manager=mManager;
        return null!=manager&&manager.putListener(callback, debug);
    }

    public AudioObject getPlaying(){
        IAudioManager manager=mManager;
        return null!=manager?manager.getPlaying():null;
    }

    public boolean resetOpenId(String openId,String debug){
        IAudioManager manager=mManager;
        return null!=manager&&manager.resetOpenId(openId, debug);
    }

    public boolean removeListener(OnAudioStatusChange callback, String debug){
        IAudioManager manager=mManager;
        return null!=manager&&manager.removeListener(callback, debug);
    }

    public boolean playVoiceFile(Context context, AudioObject voiceFilePath, OnAudioPlayFinish callback, String debug){
        IAudioManager manager=mManager;
        return null!=manager&&manager.playVoiceFile(context, voiceFilePath, callback, debug);
    }

    public boolean updateSelfPosition(Coordinate coordinate,String debug){
        IAudioManager manager=mManager;
        return null!=manager&&manager.updateSelfPosition(coordinate, debug);
    }

    public boolean enableSpeaker(boolean enable,String debug){
        IAudioManager manager=mManager;
        return null!=manager&&manager.enableSpeaker(enable, debug);
    }

    public boolean enableMicrophone(boolean enable,String debug){
        IAudioManager manager=mManager;
        return null!=manager&&manager.enableMicrophone(enable, debug);
    }

    public boolean isSpeakerEnabled(){
        IAudioManager manager=mManager;
        return null!=manager&&manager.isSpeakerEnabled();
    }

    public boolean isMicrophoneEnabled(){
        IAudioManager manager=mManager;
        return null!=manager&&manager.isMicrophoneEnabled();
    }

    public boolean entryRoom(Context context,int uid,int roomId,String debug){
        Configure configure=Configure.getInstance();
        if (null==configure||!configure.isLiveAudioEnable()){
            Debug.D("Configure disable room audio "+(null!=debug?debug:"."));
            return false;
        }
        IAudioManager manager=mManager;
        return null!=manager&&manager.entryRoom(context, uid, roomId, debug);
    }

    public boolean poll(String debug){
        IAudioManager manager=mManager;
        return null!=manager&&manager.poll(debug);
    }


    public boolean setAudioReceiveRange(int range,String debug){
        IAudioManager manager=mManager;
        return null!=manager&&manager.setAudioReceiveRange(range, debug);
    }

    public boolean exitRoom(String debug){
        IAudioManager manager=mManager;
        return null!=manager&&manager.exitRoom(debug);
    }

    public boolean deInitial(String debug){
        IAudioManager manager=mManager;
        return null!=manager&&manager.deInitial(debug);
    }

    public boolean startUploadVoiceFile(Context context, String voiceFilePath, String cloudPath, OnVoiceUploadFinish callback, String debug){
        IAudioManager manager=mManager;
        return null!=manager&&manager.startUploadVoiceFile(context,voiceFilePath,cloudPath,callback,debug);
    }

    @Override
    public boolean startRecording(Context context,String filePath, OnVoiceRecordFinish callback, Timer timer, String debug) {
        IAudioManager manager=mManager;
        return null!=manager&&manager.startRecordVoice(context,filePath,callback,debug);
    }

    @Override
    public boolean cancelRecording(String debug) {
        IAudioManager manager=mManager;
        return null!=manager&&manager.cancelRecordVoice(debug);
    }

    @Override
    public boolean stopRecording(String debug) {
        IAudioManager manager=mManager;
        return null!=manager&&manager.stopRecordVoice(debug);
    }

    @Override
    public boolean startVoiceConvertText(Context context, String filePath, OnTextConvertFinish callback, String debug) {
        IAudioManager manager=mManager;
        return null!=manager&&manager.startVoiceToText(context, filePath, callback, debug);
    }

    /////////////////////////////////////
//    @Override
//    protected boolean onStartRecording(Context context, String recordPath, OnVoiceRecordFinish callback, String debug) {
//        return AudioManager.this.startRecordVoice(context, recordPath, callback, debug);
//    }
//
//    @Override
//    protected boolean onStartUploadVoiceFile(Context context, String localPath, String cloudPath, OnVoiceUploadFinish callback, String debug) {
//        IAudioManager manager=mManager;
//        return null!=manager&&manager.startUploadVoiceFile(context, localPath, cloudPath, callback, debug);
//    }
}
