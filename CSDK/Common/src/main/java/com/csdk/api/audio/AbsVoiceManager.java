package com.csdk.api.audio;

import android.content.Context;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Create LuckMerlin
 * Date 18:24 2021/1/27
 * TODO
 */
public abstract class AbsVoiceManager implements IAudioManager {
    private Map<OnAudioStatusChange,Long> mAudioPlayListeners;

    protected final void notifyVoiceRecordFinish(int finish, long duration, String recordPath,String translate, OnVoiceRecordFinish callback){
        if (null!=callback){
            callback.onVoiceRecordFinish(finish,duration,recordPath,translate);
        }
    }

    @Override
    public boolean putListener(OnAudioStatusChange callback, String debug) {
        if (null!=callback){
            Map<OnAudioStatusChange,Long> listeners=mAudioPlayListeners;
            listeners=null!=listeners?listeners:(mAudioPlayListeners=new WeakHashMap<>());
            synchronized (listeners){
                listeners.put(callback, System.currentTimeMillis());
                return true;
            }
        }
        return false;
    }

    @Override
    public final boolean removeListener(OnAudioStatusChange callback, String debug) {
        Map<OnAudioStatusChange,Long> listeners=null!=callback?mAudioPlayListeners:null;
        if (null!=listeners){
            boolean succeed=false;
            synchronized (listeners){
                succeed=null!=listeners.remove(callback);
                if (listeners.size()<=0){
                    mAudioPlayListeners=null;
                }
            }
            return succeed;
        }
        return false;
    }

    protected final void notifyVoicePlayFinish(boolean succeed, String note, String localPath, OnAudioPlayFinish callback){
        if (null!=callback){
            callback.onAudioPlayFinish(succeed, note,localPath);
        }
    }

    protected final void notifyTextTranslateFinish(boolean succeed,Object srcLang, String srcText, Object targetLang, String targetText,OnTextTranslateFinish callback){
        if (null!=callback){
            callback.onTextTranslateFinish(succeed, srcLang, srcText, targetLang, targetText);
        }
    }

    protected final boolean notifyVoicePlayStatus(int status, String note, AudioObject localPath){
        Map<OnAudioStatusChange,Long> listeners=mAudioPlayListeners;
        if (null!=listeners){
            synchronized (listeners){
                Set<OnAudioStatusChange> set=listeners.keySet();
                if (null!=set){
                    for (OnAudioStatusChange child:set) {
                        if (null!=child){
                            child.onAudioStatusChange(status, note, localPath);
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    protected final void notifyVoiceDownloadFinish(boolean succeed, String localPath,String fileId,String note,OnVoiceDownloadFinish callback) {
        if (null!=callback){
            callback.onVoiceDownloadFinish(succeed,note,fileId,localPath);
        }
    }

    protected final void notifyVoiceUploadFinish(boolean succeed, String voiceFilePath, String fileId,String note,OnVoiceUploadFinish callback) {
        if (null!=callback){
            callback.onVoiceUploadFinish(succeed,note,voiceFilePath,fileId);
        }
    }

    protected final void notifyTextConvertFinish(boolean succeed, String note, String text, String filePath, OnTextConvertFinish callback){
        if (null!=callback){
            callback.onTextConvertFinish(succeed, note, text, filePath);
        }
    }

    protected final String generateVoiceFilePath(Context context,String prefix){
            File file=generateVoiceFile(context, prefix);
            return null!=file?file.getAbsolutePath():null;
    }

    protected final File generateVoiceFile(Context context,String prefix){
        File cacheFile=null!=context?context.getCacheDir():null;
        cacheFile=null!=cacheFile?new File(cacheFile,(null!=prefix?prefix+"_":"")+System.currentTimeMillis()):null;
        return cacheFile;
    }

}
