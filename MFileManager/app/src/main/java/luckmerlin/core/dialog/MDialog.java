package luckmerlin.core.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import com.luckmerlin.databinding.Model;

import luckmerlin.core.binding.ModelBinding;

/**
 * Create LuckMerlin
 * Date 19:17 2021/4/13
 * TODO
 */
public class MDialog extends Dialog{

    public MDialog(Context context){
        this(context,true);
    }

    public MDialog(Context context, boolean cutoutMode){
        super(context,cutoutMode);
    }

    public final MDialog setContentView(Model model){
        return setContentView(model,null);
    }

    public final MDialog setContentView(Model model,ViewGroup.LayoutParams params){
        ViewGroup decorView=null!=model?getContentGroup():null;
        if (null!=decorView){
            final View[] views=new View[1];
            views[0]=new ModelBinding().bind(decorView, model, params, new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {

                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    View current=views[0];
                    if (null!=v&&null!=current&&current==v){
                        MDialog.super.dismiss();
                    }
                }
            },"While set dialog content.");
        }
        return this;
    }

    public final MDialog immersive(){
        return immersive(Color.TRANSPARENT);
    }

    public final MDialog immersive(int color){
        new Window().applyImmersive(getWindow(),color);
        setStatusBarColor(color);
        return this;
    }

    @Override
    protected boolean onBackKeyPressed(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (super.onBackKeyPressed(dialog, keyCode, event)){
            return true;
        }
        ViewGroup vg=getContentGroup();
        int count=null!=vg?vg.getChildCount():-1;
        ModelBinding binding=new ModelBinding();
        for (int i = 0; i < count; i++) {
            View child=vg.getChildAt(i);
            Model model=null!=child?binding.findFirstModel(child):null;
            if (null!=model&&model instanceof OnBackKeyPressed&&((OnBackKeyPressed)model).onBackKeyPressed(event)){
                return true;
            }
        }
        return false;
    }

    private ViewGroup getContentGroup(){
       View view= getDecorView();
       return null!=view&&view instanceof ViewGroup?((ViewGroup)view):null;
    }

}
