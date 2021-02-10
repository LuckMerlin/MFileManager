package com.csdk.server.util;

import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;

/**
 * Create LuckMerlin
 * Date 17:09 2020/10/9
 * TODO
 */
public final class Permissions {

    public boolean isAllGranted(Context context,String ...permissions){
        if (null!=context&&null!=permissions&&permissions.length>0){
            for (String child:permissions){
                if (null!=child&&child.length()>0&&PackageManager.PERMISSION_GRANTED!=
                        ContextCompat.checkSelfPermission(context,child)){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

}
