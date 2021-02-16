package com.csdk.ui.model;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.databinding.ObservableField;

import com.csdk.api.common.Api;
import com.csdk.api.core.Debug;
import com.csdk.api.ui.Model;
import com.csdk.ui.R;

public class SoftInputModel extends Model {
    private final ObservableField<Boolean> mInputEnable=new ObservableField<>(true);
    private final ObservableField<Boolean> mInputEmoji=new ObservableField<>(true);
    private final ObservableField<Boolean> mVoiceMessageSendEnable=new ObservableField<>(true);

    public SoftInputModel(Api api) {
        super(api);
    }

    @Override
    protected void onRootAttached(String debug) {
        super.onRootAttached(debug);
        post(()->{
            Context context=getContext();
            View view=findViewById(R.id.csdk_homeModelInput_inputLITV);
            if (null!=context&&isRootAttached()&&null!=view&&view instanceof EditText){
                view.requestFocus();
                InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(view, 0);
            }
        },100,debug);
    }

    @Override
    public Object onResolveModelView(Context context) {
        return R.layout.csdk_softinput;
    }

    public ObservableField<Boolean> getInputEnable() {
        return mInputEnable;
    }

    public ObservableField<Boolean> getInputEmoji() {
        return mInputEmoji;
    }

    public ObservableField<Boolean> getVoiceMessageSendEnable() {
        return mVoiceMessageSendEnable;
    }
}
