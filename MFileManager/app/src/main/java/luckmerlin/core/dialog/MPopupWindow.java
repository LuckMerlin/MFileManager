package luckmerlin.core.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.luckmerlin.databinding.Model;

import luckmerlin.core.binding.ModelBinding;

/**
 * Create LuckMerlin
 * Date 14:31 2021/4/23
 * TODO
 */
public class MPopupWindow extends PopupWindow{

    public MPopupWindow(){
        this(0,0);
    }

    public MPopupWindow(View contentView) {
        super(contentView);
    }

    public MPopupWindow(int width, int height) {
        super(width,height);
    }

    public MPopupWindow(View contentView, int width, int height) {
        super(contentView,width,height);
    }

    public MPopupWindow(View contentView, int width, int height, boolean focusable) {
        super(contentView,width,height,focusable);
    }

    public final MPopupWindow setContentView(Context context,Model model){
        return setContentView(context, model, null);
    }

    public final MPopupWindow setContentView(Context context, Model model, ViewGroup parent){
        return setContentView(context,model,null,null,parent);
    }

    public final MPopupWindow setContentView(Context context,Model model, Integer width, Integer height){
        return setContentView(context,model,width,height,null);
    }

    public final MPopupWindow setContentView(Context context,Model model, Integer width, Integer height,ViewGroup parent){
        if (null!=context&&null!=model){
            final View[] views=new View[1];
            final ViewGroup.LayoutParams params=null!=width&&null!=height?new ViewGroup.LayoutParams(width, height):null;
            final View rootView=views[0]=new ModelBinding().createModelView(context,model,params,new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    View current=views[0];
                    if (null!=v&&null!=current&&current==v){
                        MPopupWindow.super.dismiss();
                    }
                }
            });
            if (null!=rootView&&rootView.getParent()==null){
                if (null!=parent){
                    parent.addView(rootView);
                    super.setContentView(parent,width,height);
                }else{
                    super.setContentView(rootView,width,height);
                }
            }
        }
        return this;
    }


}
