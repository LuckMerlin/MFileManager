package luckmerlin.core.binding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.Adapter;

import com.luckmerlin.databinding.Model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public final class DataBinding {
    private Method mMethod;

    public DataBinding(){
        try {
            Class cls=Class.forName("androidx.recyclerview.widget.RecyclerView");
            mMethod=null!=cls?cls.getDeclaredMethod("getAdapter" ):null;
        } catch (Exception e) {
            //Do nothing
        }
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
                Method method=mMethod;
                Object object=null!=method?method.invoke(view):null;
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
                            if (null!=object&&object instanceof Model &&dispatcher.dispatch(object)){
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

    private boolean isExtendsModelClass(Class type){
        if (null!=type){
            if (type.equals(Model.class)){
                return true;
            }
            type=type.getSuperclass();
            return null!=type&&isExtendsModelClass(type);
        }
        return false;
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

    private View attachModel(Object view,Model model) throws Exception {
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
                        child.invoke(view, model);//Get root
                        Class viewDataBindingClass=getViewDataBindingClass();
                        Method method=null!=viewDataBindingClass?viewDataBindingClass.getDeclaredMethod("getRoot"):null;
                        Object viewObject=null!=method?method.invoke(view):null;
                        return null!=viewObject&&viewObject instanceof View?((View)viewObject):null;
                    }
                }
            }
        }
        return null;
    }

    private Object getViewDataBinding(View view) throws Exception {
        Class cls=null!=view?getDataBindingUtilClass():null;
        Method viewDataBindingMethod=null!=cls?cls.getDeclaredMethod("getBinding",View.class):null;
        return null!=viewDataBindingMethod?viewDataBindingMethod.invoke(null,view):null;
    }

    private Class getViewDataBindingClass() throws ClassNotFoundException {
        return Class.forName("androidx.databinding.ViewDataBinding");
    }

    private Class getDataBindingUtilClass() throws ClassNotFoundException {
        return Class.forName("androidx.databinding.DataBindingUtil");
    }
}
