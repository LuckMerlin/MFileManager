package com.csdk.ui.model;

import android.content.Context;
import android.view.View;
import androidx.databinding.ObservableField;
import com.csdk.api.audio.AudioManager;
import com.csdk.api.audio.OnVoiceRecordFinish;
import com.csdk.api.audio.Timer;
import com.csdk.api.common.Api;
import com.csdk.api.core.Status;
import com.csdk.api.ui.Model;
import com.csdk.api.ui.OnViewClick;
import com.csdk.ui.R;

/**
 * Create LuckMerlin
 * Date 17:22 2020/8/17
 * TODO
 */
public class VoiceTextInputModel extends Model implements OnViewClick {
    private String mEmptyNotify;
    private final ObservableField<Integer> mCounter=new ObservableField<>();
    private final ObservableField<String> mContent=new ObservableField<>();
    private final ObservableField<Integer> mProgress=new ObservableField<>();
    private final ObservableField<Integer> mStatus=new ObservableField<>();
    private final int mMaxDuration=15;

    public VoiceTextInputModel(Api api) {
        super(api);
    }

    @Override
    protected void onRootAttached(String debug) {
        super.onRootAttached(debug);
        mEmptyNotify=getText(R.string.csdk_recoginze_none_result);
        startRecording("While model root attached.");
    }

    @Override
    public boolean onClicked(int viewId, View view, Object tag) {
        AudioManager manager=getAudioManager();
        if (viewId== R.id.csdk_voiceTextInput_cancelTV){
            return null!=manager&&manager.cancelRecording("While cancel view click.")&&false;
        }else if (viewId == R.id.csdk_voiceTextInput_speakFinishTV){
            mStatus.set(Status.STATUS_DOING);//Make status doing while finish click in advance
            return null!=manager&&manager.stopRecording("While speak finish view click.")||true;
        }
        return false;
    }

    public final String getContentText() {
        return mContent.get();
    }

    private  boolean startRecording(String debug){
        mStatus.set(Status.STATUS_PENDING);
        final int maxDuration=mMaxDuration;
        mProgress.set(0);//Reset
        mContent.set("");//Reset
        AudioManager manager=AudioManager.instance();
        return null!=manager&&manager.startRecording(getContext(),null,(int finish, long duration, String recordFile,String translate)-> {
            if (finish== OnVoiceRecordFinish.RECORD_NONE_PERMISSION){
                toast(R.string.csdk_permission_not_granted);
            }
            if (finish== OnVoiceRecordFinish.RECORD_SUCCEED&&null!=recordFile&&recordFile.length()>0){
                mStatus.set(Status.STATUS_DOING);
                if (null!=translate){//If need translate
                    mStatus.set(Status.STATUS_NONE);
                    mContent.set(translate);
                }else{
                    manager.startVoiceConvertText(getContext(),recordFile,(boolean succeed, String note, String text, Object src)-> {
                        mStatus.set(Status.STATUS_NONE);
                        mContent.set(text);
                    },"After record finish");
                }
            }else{
                mStatus.set(Status.STATUS_NONE);
            }
        },new Timer(maxDuration){
            @Override
            public boolean onTimerCount(int count,int max) {
                mCounter.set(maxDuration-count);
                mProgress.set(count>0&&maxDuration>0?(int)(count*100.f/maxDuration):0);
                boolean finish=count==max;
                if (finish){
                    manager.stopRecording("While timer count down." );
                }
                return finish;
            }
        },debug);
    }

    @Override
    protected void onRootDetached(String debug) {
        super.onRootDetached(debug);
        AudioManager manager=AudioManager.instance();
        if (null!=manager){
            manager.stopRecording("While model root detached.");
        }
    }

    public final ObservableField<Integer> getStatus() {
        return mStatus;
    }

    public final ObservableField<Integer> getCounter() {
        return mCounter;
    }

    public final ObservableField<Integer> getProgress() {
        return mProgress;
    }

    public final ObservableField<String> getContent() {
        return mContent;
    }

    public final String getEmptyNotify() {
        String empty=mEmptyNotify;
        return null!=empty?empty:"";
    }

    @Override
    public final Object onResolveModelView(Context context) {
        return R.layout.csdk_voice_text_input;
    }
}
