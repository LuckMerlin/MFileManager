package com.csdk.api.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.csdk.api.common.Api;
import com.csdk.api.core.Debug;
import com.csdk.api.core.OnEventChange;
import com.csdk.debug.Logger;
import com.csdk.ui.AbstractModel;
import java.lang.ref.WeakReference;

/**
 * Create LuckMerlin
 * Date 20:11 2021/1/20
 * TODO
 */
public abstract class Model extends AbstractModel {
    private Handler mHandler;
    private WeakReference<View> mRoot;

    public Model(Api api) {
        super(api);
    }

    protected void onRootAttached(String debug){
        //Do nothing
    }

    final boolean attachRoot(View root,String debug){
        if (null!=root&&null==mRoot){
            mRoot=new WeakReference<View>(root);
            Api socket=getApi();
            if (null!=socket&&this instanceof OnEventChange){
                socket.add((OnEventChange)this);
            }
            onRootAttached(debug);
            return true;
        }
        return false;
    }

    protected void onRootDetached(String debug){
        //Do nothing
    }

    final boolean detachedRoot(String debug){
        WeakReference<View> reference=mRoot;
        View view=null!=reference?reference.get():null;
        mRoot=null;
        if (null!=view){
            Api api=getApi();
            if (null!=api&&this instanceof OnEventChange){
                api.remove((OnEventChange)this);
            }
            onRootDetached(debug);
            reference.clear();
        }
        return null!=view;
    }

    protected final String getText(int textId,Object ...args){
        return getText(getContext(), textId, args);
    }

    protected final String getText(Context context, int textId, Object ...args){
        try {
            context=null!=context?context:getContext();
            return null!=context?context.getString(textId, args):null;
        }catch (Exception e){
            Logger.E("Exception get text.e="+e,e);
            e.printStackTrace();
            //Do nothing
        }
        return null;
    }

    protected final boolean toast(int textId,Object ...args){
        return toast(getContext(),textId,args);
    }

    protected final boolean toast(Context context, int textId,Object ...args){
        context=null!=context?context:getContext();
        return toast(context,getText(context,textId, args));
    }

    protected final boolean toast(final CharSequence text){
        return toast(getContext(), text);
    }

    protected final boolean toast(Context context,final CharSequence text){
        context=null!=context?context:getContext();
        if (null!=context){
            Toast.makeText(context, null!=text?text:"", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    protected final View findViewById(int viewId){
        View root=getRootView();
        return null!=root?root.findViewById(viewId):null;
    }

    protected final CharSequence getViewText(int viewId){
        return getViewText(getRootView(), viewId);
    }

    protected final CharSequence getViewText(View root,int viewId){
        View view=null!=root?root.findViewById(viewId):null;
        return null!=view&&view instanceof TextView ?((TextView)view).getText():null;
    }

    public final boolean isRootAttached(){
        return null!=getRootView();
    }

    public final View getRootView(){
        WeakReference<View> root=mRoot;
        return null!=root?root.get():null;
    }

    protected final Activity getActivity(){
        Context context=getContext();
        return null!=context&&context instanceof Activity?((Activity)context):null;
    }


    protected final Context getContext(){
        View root=getRootView();
        return null!=root?root.getContext():null;
    }

    protected final View findFocus(){
        View root=getRootView();
        root=null!=root?root.getRootView():null;
        return null!=root?root.findFocus():null;
    }

    public abstract Object onResolveModelView(Context context);

    protected final boolean post(Runnable runnable,String debug){
        return post(runnable, -1,debug);
    }

    protected final synchronized boolean post(Runnable runnable,int delay,String debug){
        if (null!=runnable){
            delay=delay<=0?0:delay;
            Handler handler=mHandler;
            handler=null!=handler?handler:(mHandler=new Handler(Looper.getMainLooper()));
            handler.postDelayed(runnable,delay);
            return true;
        }
        return false;
    }

    protected final synchronized boolean remove(Runnable runnable,String debug){
        Handler handler=null!=runnable?mHandler:null;
        if (null!=handler){
            handler.removeCallbacks(runnable);
            return true;
        }
        return false;
    }
}
