package com.luckmerlin.file;

import android.graphics.Color;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.file.api.Reply;
import com.luckmerlin.file.api.What;

import java.io.File;

public final class LocalPath extends Path implements Comparable {
    private final String mParent;
    private final String mName;
    private final String mExtension;
    private Reply<?extends Path> mSync;
    private String mMD5;

    public LocalPath(String parent,String name,String extension){
        mParent=parent;
        mName=name;
        mExtension=extension;
    }

    @Override
    public int compareTo(Object object) {
        String name=mName;
        if (null!=object&&object instanceof LocalPath){
            String pathName=((LocalPath)object).mName;
            return null!=name&&null!=pathName?name.compareTo(pathName):null==name?-1:1;
        }
        return -1;
    }

    @Override
    public Object getThumb() {
        return null;
    }

    @Override
    public boolean isLink() {
        String path=getPath();
        File file=null!=path&&path.length()>0?new File(path):null;
//        return null!=file&&file.l;
        return false;
    }

    @Override
    public String getMd5() {
        return mMD5;
    }

    public LocalPath load(boolean load, Cancel cancel){
        String path=getPath();
        File file=null!=path&&path.length()>0?new File(path):null;
        if (load&&file.isFile()&&file.length()>0){
            String md5=new MD5().getFileMD5(file,cancel);
            mMD5=null!=md5&&md5.length()>0?md5:mMD5;
        }
        return this;
    }

    public static LocalPath create(File file){
        return create(file,false,null);
    }

    public static LocalPath create(File file,boolean load, Cancel cancel){
        if (null!=file){
            final String filePath=null!=file?file.getPath():null;
            String extension=null;String name=null;String parent=null;
            final String fileDivider=File.separator;
            if (null!=filePath&&filePath.length()>0){
                final int length=filePath.length();
                int lastNameIndex=filePath.lastIndexOf(fileDivider);
                if (lastNameIndex>=0&&lastNameIndex<length){
                    parent=filePath.substring(0,lastNameIndex);
                    lastNameIndex+=1;
                    if (lastNameIndex>=0&&lastNameIndex<length){
                        name=filePath.substring(lastNameIndex,length);
                        if (null!=name){
                            final String extensionDivider=".";
                            int lastExtensionIndex=name.lastIndexOf(extensionDivider);
                            if (lastExtensionIndex>=0&&lastExtensionIndex<name.length()){
                                extension=name.substring(lastExtensionIndex);
                                name=name.substring(0,lastExtensionIndex);
                            }
                        }
                    }
                }
            }
            return new LocalPath(parent,name,extension).load(load,cancel);
        }
        return null;
    }

    public Reply<?extends Path> getSync() {
        return mSync;
    }

    public LocalPath setSync(Reply<?extends Path> sync) {
        this.mSync = sync;
        return this;
    }

    public int getSyncColor(){
        Reply<?extends Path> reply=mSync;
        Integer what=null!=reply?reply.getWhat():null;
        if (null==what){
            return Color.YELLOW;
        }else if (what== What.WHAT_NOT_EXIST){
            return Color.GRAY;
        }else if (what==What.WHAT_NORMAL){
            return Color.parseColor("#4400ff00");
        }else if (what==What.WHAT_SUCCEED&&null!=reply.getData()){
            return Color.GREEN;
        }
        return Color.RED;
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
