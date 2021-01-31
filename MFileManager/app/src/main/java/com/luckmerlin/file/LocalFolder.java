package com.luckmerlin.file;

import java.util.List;

public final class LocalFolder<A> extends Folder<A,LocalPath> {
    private final LocalPath mPath;
    private final List<LocalPath> mData;
    private final A mArg;
    private final long mFrom;
    private final long mTo;

    public LocalFolder(LocalPath path,A arg,long from,long to,List<LocalPath> data){
        mPath=path;
        mArg=arg;
        mFrom=from;
        mTo=to;
        mData=data;
    }

    @Override
    public List<LocalPath> getData() {
        return mData;
    }

    @Override
    public A getArg() {
        return mArg;
    }

    @Override
    public long getFrom() {
        return mFrom;
    }

    @Override
    public String getParent() {
        LocalPath path=mPath;
        return null!=path?path.getParent():null;
    }

    @Override
    public String getName() {
        LocalPath path=mPath;
        return null!=path?path.getName():null;
    }

    @Override
    public String getExtension() {
        LocalPath path=mPath;
        return null!=path?path.getExtension():null;
    }

    @Override
    public String getSep() {
        LocalPath path=mPath;
        return null!=path?path.getSep():null;
    }

    @Override
    public long getModifyTime() {
        LocalPath path=mPath;
        return null!=path?path.getModifyTime():0;
    }

    @Override
    public long getLength() {
        LocalPath path=mPath;
        return null!=path?path.getLength():0;
    }

    @Override
    public boolean isDirectory() {
        LocalPath path=mPath;
        return null!=path&&path.isDirectory();
    }

    @Override
    public long getTotal() {
        List<LocalPath> data=mData;
        return null!=data?data.size():0;
    }

    @Override
    public int getPermission() {
        LocalPath path=mPath;
        return null!=path?path.getPermission():Permission.PERMISSION_NONE;
    }

    @Override
    public String getMime() {
        LocalPath path=mPath;
        return null!=path?path.getMime():null;
    }
}
