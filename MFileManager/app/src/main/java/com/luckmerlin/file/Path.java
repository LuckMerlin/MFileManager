package com.luckmerlin.file;

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

    public final String getPath() {
        String sep=getSep();
        String name=getName();
        if (null==sep||sep.length()<=0){
            return null;
        }
        String parent=getParent();
        parent=null!=parent?parent:"";
        name=null!=name?name:"";
        name=name.startsWith(sep)?name.substring(1):name;
        String extension=getExtension();
        return (parent.startsWith(sep)?parent:sep+parent)+(parent.endsWith(sep)?"":sep)+name+(null!=extension?extension:"");
    }

    public final boolean isExistPermission(int...permissons){
        int length=null!=permissons?permissons.length:0;
        if (length>0){
            int permission=getPermission();
            for (int i = 0; i < length; i++) {
                if ((permissons[i]&permission)>0){
                    return true;
                }
            }
        }
        return false;
    }

    public final boolean accessible(){
        return isExistPermission(isDirectory()?(PERMISSION_READ|PERMISSION_EXECUTE):PERMISSION_READ);
    }
}
