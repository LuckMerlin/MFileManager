package com.csdk.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Create LuckMerlin
 * Date 17:00 2020/8/7
 * TODO
 */
public final class UserClassTextView extends TextView {

    public UserClassTextView(Context context) {
        this(context, null);
    }

    public UserClassTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserClassTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size=Math.max(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(size,size);
    }
}
