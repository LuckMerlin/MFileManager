package com.csdk.ui;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.csdk.api.core.Debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class HomeModelLoader {

    public List<Home> load(Context context, String debug){
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
                String value=null;Home home=null;
                final List<Home> homes=new ArrayList<>();
                for (String child:set) {
                    value=null!=child&&child.startsWith(prefix)? metaData.getString(child):null;
                    if (null!=value&&value.length()>0&&!(homes.contains(home=new Home(value)))){
                        homes.add(home);
                    }
                }
                return null!=homes&&homes.size()>0?homes:null;
            }
        } catch (Exception e) {
            Debug.E("Exception load home model list.e="+e,e);
        }
        return null;
    }

    public Object createRootViewObject(Context context,String debug){
        Home home=getHome(context,debug);
        return null!=home?home.createRootViewObject(context):null;
    }


    public Home getHome(Context context,String debug){
        List<Home> homes=null!=context?load(context,debug):null;
        return null!=homes&&homes.size()>0?homes.get(0):null;
    }
}
