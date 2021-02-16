package com.csdk.ui.model;

import android.content.Context;
import android.graphics.Rect;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.databinding.ObservableField;

import com.csdk.api.common.Api;
import com.csdk.api.core.Debug;
import com.csdk.api.ui.Model;
import com.csdk.api.ui.OnViewClick;
import com.csdk.ui.R;

public class SoftInputModel extends Model implements OnViewClick {
    private final ObservableField<Boolean> mInputEmoji=new ObservableField<>(false);
    private final ObservableField<Boolean> voice2TextInputEnable=new ObservableField<>(false);
    private final ObservableField<Boolean> mVoiceMessageSendEnable=new ObservableField<>(false);

    public SoftInputModel(Api api) {
        super(api);
    }

    protected void onSoftInputHide(){
        View view=getRootView();
        ViewParent parent=null!=view?view.getParent():null;
        if (null!=parent&&parent instanceof ViewGroup){
            ((ViewGroup)parent).removeView(view);
        }
    }

    @Override
    public boolean onClicked(int viewId, View view, Object tag) {
        if (viewId==R.id.csdk_softInput_inputRootFL){
            onSoftInputHide();
            return true;
        }
        return true;
    }

    @Override
    protected void onRootAttached(String debug) {
        super.onRootAttached(debug);
        View inputET=findViewById(R.id.csdk_homeModelInput_inputLITV);
        if (null!=inputET&&inputET instanceof EditText){
            inputET.setAlpha(0);
            post(()->{
                Rect rect = new Rect();
                View decorView=inputET.getRootView();
                final boolean[] visible=new boolean[1];
                inputET.getViewTreeObserver().addOnGlobalLayoutListener(()-> {
                    decorView.getWindowVisibleDisplayFrame(rect);
                    //If bottom more than 0.9* or equals window height,We treat as float softInput is showing,
                    //so set alpha 0 to invisible text any more
                    final int height=rect.height();
                    Context context=inputET.getContext();
                    Object winObject=context.getSystemService(Context.WINDOW_SERVICE);
                    WindowManager manager=null!=winObject&&winObject instanceof WindowManager?(WindowManager)winObject:null;
                    Display display=null!=manager?manager.getDefaultDisplay():null;
                    if (null!=display){
                        boolean isFloatSoftInput=false;
                        display.getRectSize(rect);
                        int windowHeight=rect.height();
                        if (windowHeight>height){
                            visible[0]=true;
                            float percent=windowHeight>0?((float)height/windowHeight):-1f;
                            isFloatSoftInput=percent>0.9f&&percent<=1;
                            inputET.setAlpha(isFloatSoftInput?0:1);
                            inputET.setOnTouchListener(isFloatSoftInput?(View v, MotionEvent event)-> {
                                if (null!=event&&event.getAction()==MotionEvent.ACTION_DOWN){
                                    onSoftInputHide();
                                }
                                return true;
                            }:null);
                        }else if (height==windowHeight&&visible[0]){
                            inputET.setAlpha(0);
                            onSoftInputHide();
                        }
                    }
                });
                Context context=getContext();
                if (null!=context&&isRootAttached()){
                    inputET.requestFocus();
                    InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(inputET, 0);
                }
            },100,debug);
        }
    }

    @Override
    public Object onResolveModelView(Context context) {
        return R.layout.csdk_softinput;
    }

    public ObservableField<Boolean> getVoice2TextInputEnable() {
        return voice2TextInputEnable;
    }

    public ObservableField<Boolean> getInputEmoji() {
        return mInputEmoji;
    }

    public ObservableField<Boolean> getVoiceMessageSendEnable() {
        return mVoiceMessageSendEnable;
    }
}
