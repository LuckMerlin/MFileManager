package com.luckmerlin.file;

public final class Query {
    private String mPath;
    private String mName;

    public Query(String path,String name){
        mPath=path;
        mName=name;
    }

    public String getName() {
        return mName;
    }

    public String getPath() {
        return mPath;
    }

    public boolean isEqualsName(String name){
        return isEquals(mName,name);
    }

    public boolean isEqualsPath(String path){
        return isEquals(mPath,path);
    }

    private boolean isEquals(Object arg1,Object arg2){
        return (null==arg1&&null==arg2)||(null!=arg1&&null!=arg2&&arg1.equals(arg2));
    }

    @Override
    public boolean equals(Object o) {
        if (null!=o){
            if (o instanceof Query){
                return isEquals(mName,((Query)o).mName)&&isEquals(mPath,((Query)o).mPath);
            }
        }
        return false;
    }

}
