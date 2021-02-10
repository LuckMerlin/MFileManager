package com.csdk.api.ui;

import android.view.View;
import java.lang.ref.WeakReference;

/**
 * Create LuckMerlin
 * Date 17:11 2021/1/21
 * TODO
 */
public class ModelAttachStateChangeListener implements View.OnAttachStateChangeListener{
    private final WeakReference<Model> mReference;

    public ModelAttachStateChangeListener(Model model){
        mReference=null!=model?new WeakReference<>(model):null;
    }

    @Override
    public void onViewAttachedToWindow(View v) {
        Model model=getModel();
        if (null!=model){
            model.attachRoot(v,"");
        }
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        Model model=getModel();
        if (null!=model){
            model.detachedRoot("");
        }
    }

    private Model getModel(){
        WeakReference<Model> reference=mReference;
        return null!=reference?reference.get():null;
    }
}
