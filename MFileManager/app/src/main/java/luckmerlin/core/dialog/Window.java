package luckmerlin.core.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

/**
 * Create LuckMerlin
 * Date 15:50 2021/4/21
 * TODO
 */
public final class Window {

    public boolean applyImmersive(Activity activity){
        return applyImmersive(null!=activity?activity.getWindow():null,Color.TRANSPARENT);
    }

    public boolean applyImmersive(Dialog dialog){
        return applyImmersive(null!=dialog?dialog.getWindow():null,Color.TRANSPARENT);
    }

    public boolean applyImmersive(android.view.Window window,int statusColor){
        if (null!=window){
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(statusColor);
//                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//                window.setStatusBarColor(Color.TRANSPARENT);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //使用SystemBarTint库使4.4版本状态栏变色，需要先将状态栏设置为透明
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
//            ViewGroup contentLayout = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);
//            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(activity);
//            SystemBarTintManager.SystemBarConfig config = systemBarTintManager.getConfig();
//            int actionBarHeight = config.getActionBarHeight();
//            contentLayout.getChildAt(0).setPadding(0, getStatusBarHeight(activity) + actionBarHeight, 0, 0);
//            if (mStatusBarColor == 0) {
//                setupStatusBarView(activity, contentLayout, Color.parseColor("#cccccc"));
//            } else {
//                setupStatusBarView(activity, contentLayout, mStatusBarColor);
//            }
//            // 设置Activity layout的fitsSystemWindows
//            View contentChild = contentLayout.getChildAt(0);
//            contentChild.setFitsSystemWindows(true);
        }
        return false;
    }

    public boolean setStatusBarVisible(android.view.Window window,boolean show) {
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
