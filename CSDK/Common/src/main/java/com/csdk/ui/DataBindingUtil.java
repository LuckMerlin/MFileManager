package com.csdk.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csdk.api.core.Debug;
import com.csdk.api.ui.Model;

import java.lang.reflect.Method;


public final class DataBindingUtil {

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
        Object bindingObject=null!=method?method.invoke(null, inflater,
                layoutId,parent,attach&&null!=parent):null;
        return null!=bindingObject?attachModel(bindingObject,model):null;
    }

    private View attachModel(Object view,Model model) throws Exception {
        if (null==view||null==model){
            return null;
        }else if (view instanceof View){
            Object dataBinding=getViewDataBinding((View)view);
            return null!=dataBinding?attachModel(dataBinding,model):null;
        }
        //Try as dataBinding
        Class dataBindingClass=view.getClass().getSuperclass();
        Method[] methods=null!=dataBindingClass?dataBindingClass.getDeclaredMethods():null;
        if (null!=methods&&methods.length>0) {
            Class type = null;
            for (Method child : methods) {
                if (null!=(type=(null!=child?child.getReturnType():null))&&type.equals(void.class)){
                    Class[] types=child.getParameterTypes();
                    type=null!=types&&types.length==1?types[0]:null;
                    if (null!=type&&type.equals(model.getClass())){
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
