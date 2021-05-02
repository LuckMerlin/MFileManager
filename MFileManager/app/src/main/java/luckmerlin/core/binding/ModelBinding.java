package luckmerlin.core.binding;

import android.content.Context;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.FrameLayout;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.OnModelResolve;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Create LuckMerlin
 * Date 10:50 2021/4/27
 * TODO
 */
public final class ModelBinding {
    private Object mRecycleViewMethod;

    public final Object getViewDataBinding(View view) throws Exception {
        Class cls=null!=view?getDataBindingUtilClass():null;
        Method viewDataBindingMethod=null!=cls?cls.getDeclaredMethod("getBinding",View.class):null;
        return null!=viewDataBindingMethod?viewDataBindingMethod.invoke(null,view):null;
    }

    public Class getDataBindingUtilClass() throws ClassNotFoundException {
        return Class.forName("androidx.databinding.DataBindingUtil");
    }

    public Model findFirstModel(View view){
        try {
            Object object=null!=view?getViewDataBinding(view):null;
            if (null!=object){
                Class dataBindingClass=object.getClass().getSuperclass();
                Method[] methods=null!=dataBindingClass?dataBindingClass.getDeclaredMethods():null;
                if (null!=methods&&methods.length>0) {
                    Class type = null;
                    for (Method child : methods) {
                        if (null!=(type=(null!=child?child.getReturnType():null))&&!type.equals(void.class)){
                            Class[] types=child.getParameterTypes();
                            if ((null==types||types.length<=0)&&isExtendsModelClass(type)){
                                return (Model) child.invoke(object);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            //Do nothing
        }
        return null;
    }

    public boolean isExtendsModelClass(Class type){
        if (null!=type){
            if (type.equals(Model.class)){
                return true;
            }
            type=type.getSuperclass();
            return null!=type&&isExtendsModelClass(type);
        }
        return false;
    }

    public boolean dispatch(View view, Dispatcher dispatcher){
        if (null==view||null==dispatcher){
            return false;
        }
        if (dispatcher.dispatch(view)){
            return true;
        }else if (view instanceof AbsListView){
            Adapter adapter=((AbsListView)view).getAdapter();
            if (null!=adapter&&dispatcher.dispatch(adapter)){
                return true;
            }
        }else{
            try {
                Object recycleViewMethod=mRecycleViewMethod;
                if (null==recycleViewMethod){
                    mRecycleViewMethod=false;
                    Class cls=Class.forName("androidx.recyclerview.widget.RecyclerView");
                    mRecycleViewMethod=null!=cls?cls.getDeclaredMethod("getAdapter" ):false;
                }
                Object object=null!=recycleViewMethod&&recycleViewMethod instanceof Method?((Method)recycleViewMethod).invoke(view):null;
                if (null!=object&&dispatcher.dispatch(object)){
                    return true;
                }
            } catch (Exception e) {
                //Do nothing
            }
        }
        try {
            Object dataBinding= null!=view?getViewDataBinding(view):null;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        ViewParent parent=view.getParent();
        if (null==parent){
//            Context context=view.getContext();
//            Object activityServerviceObject=null!=context?context.getSystemService(Context.ACTIVITY_SERVICE):null;
//            if (null!=activityServerviceObject&&activityServerviceObject instanceof ActivityManager){
//                ActivityManager manager=(ActivityManager)activityServerviceObject;
//                List<ActivityManager.RunningTaskInfo> taskInfos= manager.getRunningTasks(1);
//                taskInfos.get(0).baseActivity.
//            }
//            act?.getSystemService(ACTIVITY_SERVICE) as ActivityManager
//            Debug.D("QQQQQQQQQQQQQQQQQQQQQQ  "+context);
        }
        return null!=parent&&parent instanceof View&&dispatch((View)parent, dispatcher);
    }

    public View bind(final ViewGroup vg, final Model model, final String debug){
        return bind(vg, model, null,debug);
    }

    public View bind(final ViewGroup vg, final Model model, ViewGroup.LayoutParams params, final String debug){
        return bind(vg,model,params,null,debug);
    }

    public View bind(final ViewGroup vg, final Model model,ViewGroup.LayoutParams params,View.OnAttachStateChangeListener callback, final String debug){
        if(null==vg||null==model){
            return null;
        }
        View root=model.getRoot();
        if (null!=root&&null!=root.getParent()){
            return root;//Already bind
        }
        View rootView=createModelView(vg.getContext(), model, params, callback);
        if (null!=rootView&&null==rootView.getParent()){
            vg.addView(rootView, null!=params?params:new ViewGroup.LayoutParams
                    (FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            return rootView;
        }
        return null;
    }

    public View createModelView(Context context, final Model model, ViewGroup.LayoutParams params, View.OnAttachStateChangeListener callback){
        View root=model.getRoot();
        if (null!=root&&null!=root.getParent()){
            return null;//Already bind
        }
        Object modelView=null!=context&&model instanceof OnModelResolve ?((OnModelResolve)model).onResolveModel():null;
        View rootView=null;
        if (null!=modelView&&modelView instanceof Integer) {
            int layoutId = (Integer) modelView;
            LayoutInflater inflater = LayoutInflater.from(context);
            try {
                rootView = inflate(inflater, layoutId, null, false, model);
            } catch (Exception e) {
                if (null != e && e instanceof InflateException) {
                    Debug.E("Exception,Please check if enable databinding in gradle?");
                } else {
                    Debug.E("Exception inflate view.e=" + e, e);
                }
            }
            rootView=null!=rootView?rootView:inflater.inflate(layoutId,null,false);//Try for normal layout view
        }
        if (null!=rootView&&null==rootView.getParent()){
            final View finalView=rootView;
            finalView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    try {
                        Method method=Model.class.getDeclaredMethod("attachRoot",View.class,String.class);
                        if (null!=method){
                            method.setAccessible(true);
                            method.invoke(model, finalView,"After window attached.");
                        }
                    } catch (Exception e) {
                        Debug.E("Exception attach model root.e="+e,e);
                        e.printStackTrace();
                    }
                    if (null!=callback){
                        callback.onViewAttachedToWindow(v);
                    }
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    finalView.removeOnAttachStateChangeListener(this);
                    try {
                        Method method=Model.class.getDeclaredMethod("detachedRoot",String.class);
                        if (null!=method){
                            method.setAccessible(true);
                            method.invoke(model,"After window detached.");
                        }
                    } catch (Exception e) {
                        Debug.E("Exception detach model root.e="+e,e);
                        e.printStackTrace();
                    }
                    if (null!=callback){
                        callback.onViewDetachedFromWindow(v);
                    }
                }
            });
            try {
                if (null!=rootView){
                    attachModel(rootView, model);
                }
                return rootView;
            } catch (Exception e) {
                Debug.E("Exception attach model view.e="+e);
                e.printStackTrace();
                return rootView;
            }
        }
        return null;
    }

    public View inflate(LayoutInflater inflater,int layoutId,ViewGroup parent,boolean attach) throws Exception {
        return inflate(inflater,layoutId,parent,attach,null);
    }

    public View inflate(LayoutInflater inflater,int layoutId,ViewGroup parent,boolean attach,final Model model) throws Exception {
        Class dataBindingUtil=getDataBindingUtilClass();
        Method method=null!=dataBindingUtil?dataBindingUtil.getDeclaredMethod("inflate", LayoutInflater.class,
                int.class, ViewGroup.class,boolean.class):null;
        attach=attach&&null!=parent;
        Object bindingObject=null!=method?method.invoke(null, inflater, layoutId,parent,attach):null;
        try {
            bindingObject=null==bindingObject&&null!=inflater?inflater.inflate(layoutId,parent,attach):bindingObject;
        }catch (Exception e){
            //Do nothing
        }
        if (null==bindingObject){

            return null;
        }
        return attachModel(bindingObject,model);
    }

    public View attachModelSafe(Object view,Model model)  {
        try {
            return attachModel(view, model);
        } catch (Exception e) {
            Debug.E("Exception attach model.e="+e);
            e.printStackTrace();
        }
        return null;
    }

    public View attachModel(Object view,Model model) throws Exception {
        if (null==view||null==model){
            return null;
        }else if (view instanceof View){
            Object dataBinding=getViewDataBinding((View)view);
            return null!=dataBinding?attachModel(dataBinding,model):(View)view;
        }
        //Try as dataBinding
        Class dataBindingClass=view.getClass().getSuperclass();
        Method[] methods=null!=dataBindingClass?dataBindingClass.getDeclaredMethods():null;
        if (null!=methods&&methods.length>0) {
            final Class modelClass=model.getClass();
            final Class modelClassSuper=null!=modelClass?modelClass.getSuperclass():null;
            Class type = null;
            for (Method child : methods) {
                if (null!=(type=(null!=child?child.getReturnType():null))&&type.equals(void.class)){
                    Class[] types=child.getParameterTypes();
                    type=null!=types&&types.length==1?types[0]:null;
                    if (null!=type&&((null!=modelClass&&type.equals(modelClass))||(null!=modelClassSuper&&type.equals(modelClassSuper)))){
                        child.invoke(view, model);//Attach model
                        Class viewDataBindingClass=getViewDataBindingClass();
                        Method method=null!=viewDataBindingClass?viewDataBindingClass.getDeclaredMethod("getRoot"):null;
                        Object viewObject=null!=method?method.invoke(view):null;
                        return null!=viewObject&&viewObject instanceof View?((View)viewObject):null;
                    }
                }
            }
        }
        Debug.W("Fail attach model."+model+" "+view);
        return null;
    }

    public Class getViewDataBindingClass() throws ClassNotFoundException {
        return Class.forName("androidx.databinding.ViewDataBinding");
    }

}
