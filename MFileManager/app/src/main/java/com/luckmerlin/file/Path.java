package com.luckmerlin.file;

import androidx.annotation.Nullable;

public abstract class  Path implements Permission {
    public abstract String getParent();
    public abstract String getName();
    public abstract String getExtension();
    public abstract String getSep();
    public abstract long getModifyTime();
    public abstract long getLength();
    public abstract boolean isDirectory();
    public abstract long getTotal();
    public abstract int getPermission();
    public abstract String getMime();

    public final String getNameWithExtension(){
        String sep=getSep();
        if (null==sep||sep.length()<=0){
            return null;
        }
        String name=getName();
        name=null!=name?name:"";
        name=name.startsWith(sep)?name.substring(1):name;
        String extension=getExtension();
        return name+(null!=extension?extension:"");
    }

    public final String getPath() {
        String sep=getSep();
        if (null==sep||sep.length()<=0){
            return null;
        }
        String nameExtension=getNameWithExtension();
        String parent=getParent();
        parent=null!=parent?parent:"";
        return (parent.startsWith(sep)?parent:sep+parent)+(parent.endsWith(sep)?"":sep)+(null!=nameExtension?nameExtension:"");
    }

    public final boolean isExistPermission(int...permissions){
        int length=null!=permissions?permissions.length:0;
        if (length>0){
            int permission=getPermission();
            for (int i = 0; i < length; i++) {
                if ((permissions[i]&permission)>0){
                    return true;
                }
            }
        }
        return false;
    }

    public final boolean accessible(){
        return isExistPermission(isDirectory()?(PERMISSION_READ|PERMISSION_EXECUTE):PERMISSION_READ);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (null!=obj){
            if (obj instanceof Path){
                String path=((Path)obj).getPath();
                String current=getPath();
                return (null==path&&null==current)||(null!=path&&null!=current&&path.equals(current));
            }
        }
        return super.equals(obj);
    }
}
