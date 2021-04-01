package com.luckmerlin.file;

import com.luckmerlin.adapter.recycleview.Section;

public abstract class Folder<A,T> extends Path implements Section<A,T> {

    public final String getChildPath(String childName){
        String sep=getSep();
        if (null!=sep&&null!=childName&&childName.length()>0){
            childName=null!=childName&&childName.startsWith(sep)?childName:sep+childName;
            String path=getPath();
            path=null!=path&&path.endsWith(sep)?path.substring(0,path.length()-1):path;
            return null!=path&&null!=childName?path+childName:null;
        }
        return null;
    }

    @Override
    public final String getMd5(Boolean force) {
        return null;
    }
}
