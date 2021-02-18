package com.csdk.ui.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Create LuckMerlin
 * Date 17:00 2020/8/7
 * TODO
 */
public final class VoiceRecordingAnimImageView extends ImageView  {
    private boolean mVisibility= true;

    public VoiceRecordingAnimImageView(Context context) {
        this(context, null);
    }

    public VoiceRecordingAnimImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceRecordingAnimImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void refresh(){
        Drawable drawable=getDrawable();
        if (null!=drawable&&drawable instanceof AnimationDrawable){
            AnimationDrawable animationDrawable=(AnimationDrawable)drawable;
            animationDrawable.stop();
            animationDrawable.selectDrawable(0);
            if (mVisibility){//Is playing
                animationDrawable.start();
            }
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mVisibility=visibility==View.VISIBLE;
        refresh();
    }

}
