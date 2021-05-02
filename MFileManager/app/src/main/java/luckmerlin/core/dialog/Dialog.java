package luckmerlin.core.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * Create LuckMerlin
 * Date 19:17 2021/4/13
 * TODO
 */
public class Dialog implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener, DialogInterface.OnShowListener,
        DialogInterface.OnKeyListener {
    private final android.app.Dialog mDialog;

    public Dialog(Context context){
        this(context,true);
    }

    public Dialog(Context context, boolean cutoutMode){
        android.app.Dialog dialog=mDialog=null!=context?new android.app.Dialog(context):null;
        enableDisplayCutoutMode(cutoutMode);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        fullscreen(true);
        setDimAmount(0);
        dialog.setOnCancelListener((DialogInterface dlg)->onCancel(dlg));
        dialog.setOnDismissListener((DialogInterface dlg)->onDismiss(dlg));
        dialog.setOnShowListener((DialogInterface dlg)->onShow(dlg));
        dialog.setOnKeyListener((DialogInterface dlg, int keyCode, KeyEvent event)->onKey(dlg, keyCode, event));
    }

    public final View getCurrentFocus(){
        android.app.Dialog dialog=mDialog;
        return null!=dialog?dialog.getCurrentFocus():null;
    }

    public Dialog enableDisplayCutoutMode(boolean enable){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getAttributes();
            lp=null!=lp?lp:new WindowManager.LayoutParams();
            lp.layoutInDisplayCutoutMode = enable? WindowManager.LayoutParams.
                    LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES: WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
            setAttributes(lp);
        }
        return this;
    }

    protected boolean onBackKeyPressed(DialogInterface dialog, int keyCode, KeyEvent event){
        return false;
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (null!=event&&event.getAction()==KeyEvent.ACTION_UP){
            if (keyCode==KeyEvent.KEYCODE_BACK&&!onBackKeyPressed(dialog, keyCode, event)){
                return null!=dismiss()||true;
            }
        }
        return false;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

    }

    @Override
    public void onShow(DialogInterface dialog) {

    }

    public final Dialog setStatusBarColor(int color){
        Window window=getWindow();
        if (null!=window){
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(color);
                    //底部导航栏
                    //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
                }
            } catch (Exception e) {
                //Do nothing
            }
        }
        return this;
    }

    public Dialog requestWindowFeature(int featureId) {
        android.app.Dialog dialog=mDialog;
        if (null!=dialog){
            dialog.requestWindowFeature(featureId);
        }
        return this;
    }

    public final Context getContext() {
        View view=getDecorView();
        return null!=view?view.getContext():null;
    }

    public final Dialog setOnDismissListener(DialogInterface.OnDismissListener dismissListener){
        return this;
    }

    public Dialog setContentView(int resId){
        return setContentView(resId, null);
    }

    public Dialog setContentView(int resId, ViewGroup.LayoutParams params){
        android.app.Dialog dialog=mDialog;
        LayoutInflater inflater=null!=dialog?dialog.getLayoutInflater():null;
        return setContentView(null!=inflater?inflater.inflate(resId, null, false):null, params);
    }

    public Dialog setContentView(View view){
        return setContentView(view, null);
    }

    public final Dialog setContentView(View view, ViewGroup.LayoutParams params){
        android.app.Dialog dialog=null!=view&&view.getParent()==null?mDialog:null;
        if (null!=dialog){
            dialog.setContentView(view, null!=params?params:new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        return this;
    }

    public boolean show(){
        return show(null,null);
    }

    public Dialog open(){
        return open(null,null);
    }

    public Dialog open(Integer width,Integer height){
        show(width, height);
        return this;
    }

    public boolean show(Integer width,Integer height){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog&&!dialog.isShowing()){
            dialog.show();
            setLayout(null!=width?width:ViewGroup.LayoutParams.MATCH_PARENT, null!=height?height:ViewGroup.LayoutParams.MATCH_PARENT);
            return true;
        }
        return false;
    }

    public final View findViewById(int viewId){
        android.app.Dialog dialog=mDialog;
        return null!=dialog?dialog.findViewById(viewId):null;
    }

    public final Dialog dismiss(){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog&&dialog.isShowing()){
            dialog.dismiss();
        }
        return this;
    }

    public Dialog setAttributes(WindowManager.LayoutParams params){
        Window window=null!=params?getWindow():null;
        if (null!=window){
            window.setAttributes(params);
        }
        return this;
    }

    public Dialog setBackgroundDrawable(Drawable drawable){
        Window window=getWindow();
        if (null!=window){
            window.setBackgroundDrawable(drawable);
        }
        return this;
    }

    public final WindowManager.LayoutParams getAttributes(){
        Window window=getWindow();
        return null!=window?window.getAttributes():null;
    }

    public final Dialog setDimAmount(float dim){
        Window window=getWindow();
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

    public final Dialog setSoftInputMode(int softInputMode){
        Window window=getWindow();
        if (null!=window){
            window.setSoftInputMode(softInputMode);
        }
        return this;
    }

    public final Dialog setLayout(int width, int height){
        Window window=getWindow();
        if (null!=window){
            window.setLayout(width, height);
        }
        return this;
    }

    public final Dialog fullscreen(boolean full){
        return full?setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.
                LayoutParams.FLAG_FULLSCREEN):clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public final Dialog setFlags(int flags, int mask){
        Window window=getWindow();
        if (null!=window){
            window.setFlags(flags, mask);
        }
        return this;
    }

    public final Dialog clearFlags(int flags){
        Window window=getWindow();
        if (null!=window){
            window.clearFlags(flags);
        }
        return this;
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

}
