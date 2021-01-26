package com.luckmerlin.file;

public abstract class  Path {
    public abstract String getParent();
    public abstract String getName();
    public abstract String getExtension();
    public abstract String getSep();
    public abstract long getModifyTime();
    public abstract long getLength();
    public abstract boolean isDirectory();
    public abstract boolean accessible();
    public abstract long getTotal();

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
}
