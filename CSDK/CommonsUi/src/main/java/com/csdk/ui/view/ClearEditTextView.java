package com.csdk.ui.view;

import android.content.Context;
import android.text.Selection;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

/**
 * Create LuckMerlin
 * Date 17:00 2020/8/7
 * TODO
 */
public final class ClearEditTextView extends EditText {

    public ClearEditTextView(Context context) {
        this(context, null);
    }

    public ClearEditTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClearEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new ZanyInputConnection(super.onCreateInputConnection(outAttrs),true);
    }


    private class ZanyInputConnection extends InputConnectionWrapper {
        public ZanyInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }
        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                ClearEditTextView edit = ClearEditTextView.this;
                String text = edit.getText().toString();
                if(text.length() > 0){
                    String newText = text.substring(0,text.length() - 1);
                    edit.setText(newText);
                    Selection.setSelection(edit.getText(), newText.length());
                }
                return false;
            }
            return super.sendKeyEvent(event);
        }
    }

}
