package com.csdk.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Create LuckMerlin
 * Date 17:00 2020/8/7
 * TODO
 */
public final class AutoCloseOutlineRecyclerView extends TouchDisableRecyclerView {

    public AutoCloseOutlineRecyclerView(Context context) {
        this(context, null);
    }

    public AutoCloseOutlineRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoCloseOutlineRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return getVisibility()!= View.VISIBLE;
    }
}
