package com.csdk.ui;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.csdk.api.common.Api;
import com.csdk.api.core.Debug;
import com.csdk.api.ui.Model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class HomeModelLoader {

    public List<String> load(Context context, String debug){
        final String prefix="csdkHomeUi";
        final String pkgName=null!=context?context.getPackageName():null;
        if (null==pkgName||pkgName.length()<=0){
            Debug.W("Can't load home model list while package name invalid "+(null!=debug?debug:"."));
            return null;
        }
        PackageManager pm=context.getPackageManager();
        PackageInfo packageInfo= null;
        try {
            packageInfo = null!=pm?pm.getPackageInfo(pkgName, PackageManager.GET_META_DATA):null;
            ApplicationInfo applicationInfo=null!=packageInfo?packageInfo.applicationInfo:null;
            Bundle metaData=null!=applicationInfo?applicationInfo.metaData:null;
            Set<String> set=null!=metaData?metaData.keySet():null;
            if (null!=set&&set.size()>0){
                String value=null;
                final List<String> homes=new ArrayList<>();
                for (String child:set) {
                    value=null!=child&&child.startsWith(prefix)? metaData.getString(child):null;
                    if (null!=value&&value.length()>0&&!(homes.contains(value))){
                        homes.add(value);
                    }
                }
                return null!=homes&&homes.size()>0?homes:null;
            }
        } catch (Exception e) {
            Debug.E("Exception load home model list.e="+e,e);
        }
        return null;
    }

    public Model getHomeModel(Context context, Api api,String debug){
        String homeModel=getHome(context,debug);
        try {
            Class modelClass=null!=homeModel&&homeModel.length()>0?Class.forName(homeModel):null;
            Constructor constructor=null!=modelClass?modelClass.getDeclaredConstructor(Api.class):null;
            Object modelObject=null!=constructor?constructor.newInstance(api):null;
            return null!=modelObject&&modelObject instanceof Model?((Model)modelObject):null;
//            Constructor[] constructors=null!=modelClass?modelClass.getDeclaredConstructors():null;
//            if (null==constructors||constructors.length>1){
//                Debug.W("Home model constructor must define only one.");
//                return null;
//            }
//            for (Constructor constructor:constructors) {
//                Class[] parameters=null!=constructor?constructor.getParameterTypes():null;
//                null!=parameters&&parameters.length>0?parameters[0]:null;
//                if (null!=parameters&&parameters.length>0){
//
//                }
//            }
        } catch (Exception e) {
            Debug.E("Exception create home model.e="+e,e
            );
            e.printStackTrace();
        }

        return null;
    }


    public String getHome(Context context,String debug){
        List<String> homes=null!=context?load(context,debug):null;
        return null!=homes&&homes.size()>0?homes.get(0):null;
    }
}
