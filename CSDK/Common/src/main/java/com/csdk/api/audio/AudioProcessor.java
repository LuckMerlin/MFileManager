package com.csdk.api.audio;

import android.content.Context;
import com.csdk.api.audio.OnTextConvertFinish;
import com.csdk.api.audio.OnVoiceRecordFinish;
import com.csdk.api.audio.Timer;

/**
 * Create LuckMerlin
 * Date 13:57 2020/10/15
 * TODO
 */
public interface AudioProcessor {
    public boolean startRecording(Context context,String filePath,OnVoiceRecordFinish callback,Timer timer,String debug);
    public boolean cancelRecording(String debug);
    public boolean stopRecording(String debug);
    public boolean startVoiceConvertText(Context context,String filePath,OnTextConvertFinish callback,String debug);
}
