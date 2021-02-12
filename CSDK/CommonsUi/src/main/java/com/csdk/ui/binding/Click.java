package com.csdk.ui.binding;

import android.view.View;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.csdk.api.ui.ClickListener;
import com.csdk.api.ui.Model;
import com.csdk.api.ui.OnViewClick;
import com.csdk.api.ui.OnViewLongClick;

import java.lang.reflect.Field;

/**
 * Create LuckMerlin
 * Date 18:50 2021/2/1
 * TODO
 */
public final class Click {
    public final static int NONE=0;
    public final static int CLICK=1;
    public final static int LONG_CLICK=2;
    private final int mClick;
    private final ClickListener mListener;
    private Object mTag;

    private Click(int click, ClickListener listener) {
        mClick=click;
        mListener=listener;
    }

    public static Click click(int click){
        return new Click(click,null);
    }

    public Click tag(Object tag){
        mTag=tag;
        return this;
    }

    public static Click click(){
        return click(null);
    }

    public static Click click(ClickListener listener){
        return click(CLICK,listener);
    }

    public static Click click(int click, ClickListener listener){
        return new Click(click,listener);
    }

    private static boolean dispatch(View view, Dispatcher dispatcher){
        if (null==view||null==dispatcher){
            return false;
        }
        if (dispatcher.dispatch(view)){
            return false;
        }
        ViewDataBinding dataBinding=DataBindingUtil.getBinding(view);
        Class cls=null!=dataBinding?dataBinding.getClass():null;
        Field[] fields=null!=cls?cls.getSuperclass().getDeclaredFields():null;
        if (null!=fields&&fields.length>0){
            for (Field child:fields) {
                try {
                    if (null!=child){
                        child.setAccessible(true);
                        Object object=child.get(dataBinding);
                        if (null!=object&&object instanceof Model&&dispatcher.dispatch(object)){
                            return true;
                        }
                    }
                }catch (Exception e){
                    //Do nothing
                }
            }
        }
        ViewParent parent=view.getParent();
        return null!=parent&&parent instanceof View&&dispatch((View)parent, dispatcher);
    }

    @BindingAdapter("clickEnable")
    public static void setClickEnable(View view, Click click){
        if (null!=view&&null!=click) {
            final int clickValue=click.mClick;
            final Object tag=click.mTag;
            final ClickListener listener=click.mListener;
            if ((clickValue&CLICK)>0){
                view.setOnClickListener((v)->{
                    if (null!=v){
                        AlphaAnimation animation=new AlphaAnimation(0.5f,1);
                        animation.setDuration(200);
                        animation.setInterpolator(new AccelerateInterpolator());
                        v.startAnimation(animation);
                    }
                    final int viewId=null!=v?v.getId():0;
                    if (null!=listener&&listener instanceof OnViewClick&&((OnViewClick)listener).onClicked(viewId, v,tag)){
                        return;
                    }
                    dispatch(v, (arg)-> null!=arg&&arg instanceof OnViewClick&&((OnViewClick)arg).onClicked(viewId, v,tag));
                });
            }
            if ((clickValue&LONG_CLICK)>0){
                view.setOnLongClickListener((v)-> null!=listener&&listener instanceof OnViewLongClick&& ((OnViewLongClick)listener).onLongClicked(null!=v?v.getId():0, v,tag));
            }
        }
    }

    private interface Dispatcher{
        boolean dispatch(Object object);
    }
}
