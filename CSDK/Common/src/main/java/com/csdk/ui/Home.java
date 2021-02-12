package com.csdk.ui;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;

import com.csdk.api.core.Debug;

public final class Home {
    private final String mHomeDef;

    public Home(String homeDef){
        mHomeDef=homeDef;
    }

    public Object createRootViewObject(Context context){
        String homeDef = mHomeDef;
        Resources resources=null!=context?context.getResources():null;
        if (null==resources||null==homeDef||homeDef.length()<=0){
            Debug.W("Can't create home root view while context or home def invalid.");
            return null;
        }
        if (homeDef.startsWith("res/layout/")){
            homeDef=homeDef.replaceAll("res/layout/","");
            homeDef=homeDef.endsWith(".xml")?homeDef.replaceAll(".xml",""):homeDef;
            int layoutId=resources.getIdentifier(homeDef,"layout",context.getPackageName());
            if (layoutId!=0&&layoutId!=-1){
                return layoutId;
            }
        }
        return null;
    }
}
