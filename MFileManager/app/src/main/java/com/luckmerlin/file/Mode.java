package com.luckmerlin.file;

import java.util.ArrayList;

public final class Mode {
    public final static int MODE_MULTI_CHOOSE=R.string.multiChoose;
    public final static int MODE_DOWNLOAD=R.string.download;
    public final static int MODE_UPLOAD=R.string.upload;
    public final static int MODE_MOVE=R.string.move;
    public final static int MODE_COPY=R.string.copy;
    private ArrayList<Path> mArgs;
    private final int mMode;

    public Mode(int mode){
        this(mode,null);
    }

    public Mode(int mode,ArrayList<Path> args){
        mMode=mode;
        mArgs=args;
    }

    public Mode add(Path arg){
        if (null!=arg){
            ArrayList<Path> args=mArgs;
            args=null!=args?args:(mArgs=new ArrayList<>());
            synchronized (args){
                if (!args.contains(arg)){
                    args.add(arg);
                }
            }
        }
        return this;
    }

    public boolean cleanArgs(){
        ArrayList<Path> args=mArgs;
        if (null!=args){
            args.clear();
            mArgs=null;
            return true;
        }
        return false;
    }

    public ArrayList<Path> getArgs() {
        return mArgs;
    }

    public int getMode() {
        return mMode;
    }
}
