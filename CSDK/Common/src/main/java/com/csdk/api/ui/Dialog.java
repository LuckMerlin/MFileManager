package com.csdk.api.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * Create LuckMerlin
 * Date 15:27 2020/8/6
 * TODO
 */
public final class Dialog {
    private final android.app.Dialog mDialog;

    public Dialog(Context context){
        this(context,true);
    }

    public Dialog(Context context, boolean enableEdge){
        this(context,enableEdge,true);
    }

    public Dialog(Context context, boolean enableEdge, boolean hideBottomNavigation){
        android.app.Dialog dialog=mDialog=new android.app.Dialog(context);
        Window window=dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (hideBottomNavigation){
            View decoreView=window.getDecorView();
            if (null!=decoreView){
                final int uiOptions =( View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                decoreView.setSystemUiVisibility(uiOptions);
                decoreView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                           if (Build.VERSION.SDK_INT >= 19) {
                               decoreView.setSystemUiVisibility(uiOptions|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                        } else {
                               decoreView.setSystemUiVisibility(uiOptions|View.SYSTEM_UI_FLAG_LOW_PROFILE);
                        }
                    }
                });
            }
        }
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (enableEdge&&Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = window.getAttributes();
            if (null!=lp){
                lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                window.setAttributes(lp);
            }
        }
    }

    public Dialog setSoftInputMode(int softInputMode){
        Window window=getWindow();
        if (null!=window){
            window.setSoftInputMode(softInputMode);
        }
        return this;
    }

    public Dialog setFlags(int flags, int mask){
        Window window=getWindow();
        if (null!=window){
            window.setFlags(flags, mask);
        }
        return this;
    }

    public final Dialog setContentView(Model model){
        return setContentView(model,null,null);
    }

    public final Dialog setContentView(Model model,Integer width,Integer height){
        if (null!=model) {
            View view=getRoot();
            if (null!=view&&view instanceof ViewGroup){
                new ModelBinder().bind((ViewGroup)view, model, null);
            }
            Window window=getWindow();
            if (null!=window){
                window.setLayout(null!=width?width:ViewGroup.LayoutParams.WRAP_CONTENT,
                        null!=height?height:ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        }
        return this;
    }

    public final Dialog setDimAmount(float dim){
        android.app.Dialog dialog=mDialog;
        Window window=null!=dialog?dialog.getWindow():null;
        if (null!=window){
            window.setDimAmount(dim);
        }
        return this;
    }

    public final Dialog setCanceledOnTouchOutside(boolean cancel){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog){
            dialog.setCanceledOnTouchOutside(cancel);
        }
        return this;
    }

    public final Dialog setCancelable(boolean flag){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog){
            dialog.setCancelable(flag);
        }
        return this;
    }

    public final Dialog setOnDismissListener(DialogInterface.OnDismissListener dismissListener){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog){
            dialog.setOnDismissListener(dismissListener);
        }
        return this;
    }

    public final Dialog setContentView(int layoutId){
        return setContentView(layoutId,null,null);
    }

    public final Dialog setContentView(int layoutId,Integer width,Integer height){
        android.app.Dialog dialog=mDialog;
        Context context=null!=dialog?dialog.getContext():null;
        if (null!=context) {
            setContentView(View.inflate(context, layoutId, null),width,height);
        }
        return this;
    }

    public final View getRoot(){
        android.app.Dialog dialog=mDialog;
        Window window=null!=dialog?dialog.getWindow():null;
        return null!=window?window.getDecorView():null;
    }

    public final Dialog setContentView(View view){
        return setContentView(view, null,null);
    }

    public final Dialog setContentView(View view,Integer width,Integer height){
        android.app.Dialog dialog=null!=view?mDialog:null;
        if (null!=dialog){
            dialog.setContentView(view);
            Window window=getWindow();
            if (null!=window){
                window.setLayout(null!=width?width:ViewGroup.LayoutParams.WRAP_CONTENT,
                        null!=height?height:ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        }
        return this;
    }

    public final boolean show(boolean show){
        return show?show():dismiss();
    }

    public final boolean show(){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog){
            Window window=getWindow();
            if (null!=window){
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            }
            dialog.show();
            View decorView=null!=window?window.getDecorView():null;
            if (null!=decorView){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN;
                    decorView.setSystemUiVisibility(uiOptions);
                }
            }
            if (null!=window){
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            }
            return true;
        }
        return false;
    }

    public final Dialog setGravity(int gravity){
        Window window=getWindow();
        if (null!=window){
            window.setGravity(gravity);
        }
        return this;
    }

    public final View getDecorView(){
        Window window=getWindow();
        return null!=window?window.getDecorView():null;
    }

    public final Window getWindow(){
        android.app.Dialog dialog=mDialog;
        return null!=dialog?dialog.getWindow():null;
    }

    public final boolean isShowing(){
        android.app.Dialog dialog=mDialog;
        return null!=dialog&&dialog.isShowing();
    }

    public final boolean dismiss(){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog){
            dialog.dismiss();
            return true;
        }
        return false;
    }

//    public static boolean checkNavigationBarShow(Context context, Window window) {
//        int systemUiVisility = ((Activity)context).getWindow().getDetectorView().getSystemUiVisilility();
//        if(systemUiVisility & (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
//                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION))==0) {
//            return true;
//        }
//        return false;
//    }
}
