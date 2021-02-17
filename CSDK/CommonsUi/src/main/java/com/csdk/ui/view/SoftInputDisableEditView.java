package com.csdk.ui.view;

import android.content.Context;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import com.csdk.api.core.Debug;
import com.csdk.api.ui.Dialog;
import com.csdk.api.ui.OnViewClick;
import com.csdk.ui.R;
import com.csdk.ui.model.SoftInputModel;

/**
 * Create LuckMerlin
 * Date 20:06 2020/12/23
 * TODO
 */
public class SoftInputDisableEditView extends EditText implements OnViewClick {

    public SoftInputDisableEditView(Context context) {
        this(context, null);
    }

    public SoftInputDisableEditView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SoftInputDisableEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onClicked(int viewId, View view, Object tag) {
        if (viewId== R.id.csdk_homeModelInput_inputLITV){
            return startInputSoft("While input view click.");
        }
        return false;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (null!=text&&text instanceof SpannableStringBuilder){
            setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private boolean startInputSoft(String debug){
        Context context=getContext();
        if (null==context){
            Debug.W("Can't start input soft while context NULL "+(null!=debug?debug:"."));
            return false;
        }
        final Dialog dialog=new Dialog(context);
        CharSequence charSequence=getText();
        Debug.D("QQQQQQQQQQQq  "+(null!=charSequence?charSequence.getClass():null));
        return dialog.setContentView(new SoftInputModel(null,null),ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                |WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE).setGravity(Gravity.BOTTOM).
                setCanceledOnTouchOutside(true).setCancelable(true).setDimAmount(0).show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int inputType=getInputType();
        setInputType(InputType.TYPE_NULL);
        boolean succeed=super.onTouchEvent(event);
        setInputType(inputType);
        return succeed;
    }

    @Override
    public boolean isTextSelectable() {
        return true;
    }

    @Override
    public boolean isFocused() {
        return true;
    }

}

