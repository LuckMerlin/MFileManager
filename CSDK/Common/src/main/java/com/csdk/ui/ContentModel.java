package com.csdk.ui;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.csdk.api.core.Code;
import com.csdk.api.core.Debug;
import com.csdk.api.ui.Model;
import com.csdk.api.ui.ModelAttachStateChangeListener;
import com.csdk.api.ui.ModelBinder;
import com.csdk.debug.Logger;

import java.lang.ref.WeakReference;

/**
 * Create LuckMerlin
 * Date 14:51 2021/1/21
 * TODO
 */
public final class ContentModel {

   public  int setContentView(Activity activity,final Object contentViewObj, FrameLayout.LayoutParams params){
        if (null==contentViewObj){
            Logger.W("Can't set csdk content view while view NULL.");
            return Code.CODE_PARAMS_INVALID;
        }
        if (null==activity){
            Logger.W("Can't set csdk content view while context NULL,Check if initial?");
            return Code.CODE_PARAMS_INVALID;
        }
        Window window=activity.getWindow();
        View decoreView=null!=window?window.getDecorView():null;
        if (null==decoreView||!(decoreView instanceof ViewGroup)){
            Logger.W("Can't set csdk content view while root invalid.");
            return Code.CODE_FAIL;
        }else if (activity.isFinishing()||(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1&&activity.isDestroyed())){
            Logger.W("Can't set csdk content view while activity finished.");
            return Code.CODE_FAIL;
        }
        final ViewGroup activityRoot=(ViewGroup)decoreView;
        //
        final View[] contentView=new View[1];
        activityRoot.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                View content=contentView[0];
                if (null!=content){
                    activityRoot.bringChildToFront(content);//Bring self to top
                }
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
                View content=contentView[0];
                if (null!=content){
                    if (null!=child&&child== content){//Self remove
                        activityRoot.setOnHierarchyChangeListener(null);
                    }else{
                        activityRoot.bringChildToFront(content);//Bring self to top
                    }
                }
            }
        });
        if (contentViewObj instanceof View){
            View view=(View)contentViewObj;
            if (view.getParent()!=null){
                Logger.W("Can't set csdk content view while view already added into others viewGroup.");
                activityRoot.setOnHierarchyChangeListener(null);
                return Code.CODE_FAIL;
            }
            params=null!=params?params:new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            activityRoot.addView(view,params);
            contentView[0]=view;
        }else if (contentViewObj instanceof Model){
            contentView[0]=new ModelBinder().bind(activityRoot, (Model)contentViewObj,"While api call.");
        }
        final View finalContentView=contentView[0];
        if (null==finalContentView){
            Logger.W("Can't set csdk content view while content view invalid.");
            activityRoot.setOnHierarchyChangeListener(null);
            return Code.CODE_FAIL;
        }
        ViewParent parent=finalContentView.getParent();
        if (null==parent||parent!=activityRoot){
            Logger.W("Can't set csdk content view while view attach fail.");
            activityRoot.setOnHierarchyChangeListener(null);
            if (null!=parent&&parent instanceof ViewGroup){
                ((ViewGroup)parent).removeView(finalContentView);
            }
            return Code.CODE_FAIL;
        }
        finalContentView.addOnAttachStateChangeListener(new ModelAttachStateChangeListener
                (null!=contentViewObj&& contentViewObj instanceof Model?((Model)contentViewObj):null) {
            @Override
            public void onViewDetachedFromWindow(View v) {
                finalContentView.removeOnAttachStateChangeListener(this);
            }
        });
        Context appContext=activity.getApplicationContext();
        Application application=null!=appContext&&appContext instanceof Application?((Application)appContext):null;
        final WeakReference<Activity> weakReference=new WeakReference<>(activity);
        application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityDestroyed(@NonNull Activity act) {
                Activity contentActivity=weakReference.get();
                if(null!=act&&null!=contentActivity&&act == contentActivity){//Check if self activity destroyed
                    application.unregisterActivityLifecycleCallbacks(this);
                    contentView[0]=null;
                    activityRoot.removeView(finalContentView);
                }
            }
        });
        Logger.D("Succeed set content view.");
        return Code.CODE_SUCCEED;
    }

}
