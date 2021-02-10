package com.csdk.api.ui;

import android.content.Context;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.csdk.api.core.Debug;
import com.csdk.api.ui.Model;
import com.csdk.debug.Logger;
import com.csdk.server.data.OnFrameReceive;
import com.csdk.server.socket.HeroSocket;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Create LuckMerlin
 * Date 16:46 2020/8/17
 * TODO
 */
 public final class ModelBinder {

    public View bind(final ViewGroup vg, final Model model, final String debug){
        if(null==vg||null==model){
            return null;
        }
        View root=model.getRootView();
        if (null!=root&&null!=root.getParent()){
            return root;//Already bind
        }
        Context context=vg.getContext();
        Object modelView=null!=context?model.onResolveModelView(context):null;
        View rootView=null;
        if (null!=modelView&&modelView instanceof Integer){
            int layoutId=(Integer)modelView;
            LayoutInflater inflater=LayoutInflater.from(context);
            try {//Try for dataBinding layout view
                Class utilClass=Class.forName("androidx.databinding.DataBindingUtil");
                Class bindingClass=Class.forName("androidx.databinding.ViewDataBinding");
                if (null!=utilClass&&null!=bindingClass){
                    Method method=utilClass.getDeclaredMethod("inflate",LayoutInflater.class, int.class,ViewGroup.class,boolean.class);
                    Object dataBindingObject=null!=method?method.invoke(null, inflater,modelView,null,false):null;
                    Method rootGetMethod=bindingClass.getDeclaredMethod("getRoot");
                    Object rootViewObj=null!=rootGetMethod?rootGetMethod.invoke(dataBindingObject):null;
                    if (null!=rootViewObj&&rootViewObj instanceof View){
                        rootView=(View)rootViewObj;
                    }
                    if (null!=dataBindingObject){
                        Class dataBindingClass=dataBindingObject.getClass().getSuperclass();
                        Method[] methods=dataBindingClass.getDeclaredMethods();
                        if (null!=methods&&methods.length>0){
                            Class type=null;
                            for (Method child:methods) {
                                if (null!=(type=(null!=child?child.getReturnType():null))&&type.equals(void.class)){
                                    Class[] types=child.getParameterTypes();
                                    type=null!=types&&types.length==1?types[0]:null;
                                    if (null!=type&&type.equals(model.getClass())){
                                        child.invoke(dataBindingObject, model);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }catch (Exception e){
                if (null!=e&&e instanceof InflateException){
                    Logger.E("Exception,Please check if enable databinding in gradle?");
                }
            }
            if (null==rootView){//Fail create databinding view
                try {//Try for normal layout view
                    int count=vg.getChildCount();
                    List<View> views=new ArrayList<>(count);
                    View child=null;
                    for (int i = 0; i < count; i++) {
                        if (null!=(child=vg.getChildAt(i))){
                            views.add(child);
                        }
                    }
                    if (null!=inflater.inflate(layoutId,null,false)){
                        count=vg.getChildCount();
                        for (int i = count-1; i >=0; i--) {
                            if (null!=(child=vg.getChildAt(i))&&!views.contains(child)){
                                rootView=child;//Found just inflate child
                                break;
                            }
                        }
                    }
                }catch (Exception e2){
                    //Do nothing
                }
            }
            if (null!=rootView&&null==rootView.getParent()){
                final View finalView=rootView;
                finalView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {
                        model.attachRoot(finalView, "After window attached.");
                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {
                        finalView.removeOnAttachStateChangeListener(this);
                        model.detachedRoot( "After window detached.");
                    }
                });
                vg.addView(finalView, new ViewGroup.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            }
            return rootView;
        }
        return null;
    }
}
