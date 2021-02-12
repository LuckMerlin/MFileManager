package com.csdk.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Create LuckMerlin
 * Date 17:00 2020/8/7
 * TODO
 */
public final class InputRootLayout extends LinearLayout  {

    public InputRootLayout(Context context) {
        this(context, null);
    }

    public InputRootLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputRootLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return isEnabled()?super.dispatchTouchEvent(ev):true;
    }

}
