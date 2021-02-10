package com.csdk.api.data;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Create LuckMerlin
 * Date 18:19 2021/1/12
 * TODO
 */
public final class ActivityUtil {

    public final boolean applyFullscreen(Activity activity){
        Window window=null!=activity?activity.getWindow():null;
        View decoreView=null!=window?window.getDecorView():null;
        if (null!=decoreView){
            WindowManager.LayoutParams lp = window.getAttributes();
            if (null!=window&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (null!=lp){
                    lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                    window.setAttributes(lp);
                }
            }
            window.setAttributes(lp);
            decoreView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            decoreView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                    uiOptions |= 0x00001000;
                    decoreView.setSystemUiVisibility(uiOptions);
                }
            });
            return true;
        }
        return false;
    }
}
