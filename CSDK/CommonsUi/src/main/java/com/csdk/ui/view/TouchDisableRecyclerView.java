package com.csdk.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Create LuckMerlin
 * Date 17:00 2020/8/7
 * TODO
 */
public class TouchDisableRecyclerView extends RecyclerView {

    public TouchDisableRecyclerView(Context context) {
        this(context, null);
    }

    public TouchDisableRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchDisableRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return false;
    }
}
