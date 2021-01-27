package com.luckmerlin.file;

import java.io.File;

public final class LocalPath extends Path {
    private final String mParent;
    private final String mName;
    private final String mExtension;

    public LocalPath(String parent,String name,String extension){
        mParent=parent;
        mName=name;
        mExtension=extension;
    }

    @Override
    public String getParent() {
        return mParent;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public String getSep() {
        return File.separator;
    }

    @Override
    public long getModifyTime() {
        File file=getFile();
        return null!=file?file.lastModified():0;
    }

    @Override
    public int getPermission() {
        File file=getFile();
        int permission=PERMISSION_NONE;
        if (null!=file){
            permission=permission|(file.canExecute()?PERMISSION_EXECUTE:permission);
            permission=permission|(file.canRead()?PERMISSION_READ:permission);
            permission=permission|(file.canWrite()?PERMISSION_WRITE:permission);
        }
        return permission;
    }

    @Override
    public String getMime() {
        return null;
    }

    @Override
    public long getTotal() {
        File file=getFile();
        String[] names=null!=file&&file.isDirectory()?file.list():null;
        return null!=names?names.length:0;
    }

    @Override
    public long getLength() {
        File file=getFile();
        return null!=file?file.length():0;
    }

    @Override
    public boolean isDirectory() {
        File file=getFile();
        return null!=file&&file.isDirectory();
    }

    @Override
    public String getExtension() {
        return mExtension;
    }

    public final File getFile(){
        String path=getPath();
        return null!=path&&path.length()>0?new File(path):null;
    }
}
