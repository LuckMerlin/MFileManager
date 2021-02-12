package com.csdk.ui.ue4;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Create LuckMerlin
 * Date 15:00 2020/11/30
 * TODO
 */
public final class SoftInputCloser {

    public boolean close(View root,String debug){
        View focusView=null!=root?root.findFocus():null;
        if (null!=focusView){
            InputMethodManager inputManager = (InputMethodManager) focusView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(focusView.findFocus().getWindowToken(), 0);
            return true;
        }
        return false;
    }
}
