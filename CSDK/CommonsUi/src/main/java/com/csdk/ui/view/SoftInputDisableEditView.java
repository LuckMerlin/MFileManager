package com.csdk.ui.view;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import com.csdk.api.core.Debug;
import com.csdk.api.struct.Struct;
import com.csdk.api.struct.StructArrayList;
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
    private StructArrayList mStructArrayList;

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
        mStructArrayList=null;
        if (null!=text&&text instanceof StructArrayList){
            StructArrayList list=(StructArrayList)text;
            mStructArrayList=list;
            setMovementMethod(LinkMovementMethod.getInstance());
            post(()->setSelection(list.getSelectStart(),list.getSelectEnd()));
        }
    }

    private boolean startInputSoft(String debug){
        Context context=getContext();
        if (null==context){
            Debug.W("Can't start input soft while context NULL "+(null!=debug?debug:"."));
            return false;
        }
        final Dialog dialog=new Dialog(context);
        final StructArrayList arrayList=mStructArrayList;
        final SoftInputModel model=new SoftInputModel(null,null!=arrayList?arrayList.
                setSelectStart(getSelectionStart()).setSelectEnd(getSelectionEnd()):null);
        dialog.setOnDismissListener((DialogInterface dlg)-> {
            setText(model.getInputTextStruct());
            Debug.D("AAAAAAAAAAAAa "+model.getInputTextStruct().toText());
        });
        return dialog.setContentView(model, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                |WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE).setGravity(Gravity.BOTTOM).
                setCanceledOnTouchOutside(false).setCancelable(false).setDimAmount(0).show();
    }


    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (start>=0){
            StructArrayList structs=mStructArrayList;
            structs=null!=structs?structs:(mStructArrayList=new StructArrayList());
//          structs.add();
        }
        Debug.D("AAAAonTextChanged AAAAAAAa  "+start+" "+lengthBefore+" "+lengthAfter+" "+text);

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

