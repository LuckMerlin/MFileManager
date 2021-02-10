package com.csdk.server;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

/**
 * Create LuckMerlin
 * Date 16:27 2021/1/4
 * TODO
 */
public final class Language {

    public String getSystemLanguage(Context context){
        Resources resources=null!=context?context.getResources():null;
        Configuration configuration=null!=resources?resources.getConfiguration():null;
        return null!=configuration?getSystemLanguage(configuration):null;
    }

    public String getSystemLanguage(Configuration configuration){
        Locale locale = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = configuration.getLocales().get(0);
        }
        locale =null!=locale?locale:configuration.locale;
        String language= null!=locale?locale.getLanguage():null;
        return null!=language&&language.length()>0?language.toLowerCase():null;
    }
}
