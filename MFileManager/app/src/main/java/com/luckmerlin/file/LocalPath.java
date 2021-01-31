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

    public static LocalPath create(File file){
        if (null!=file){
            final String filePath=null!=file?file.getPath():null;
            String extension=null;String name=null;String parent=null;
            if (null!=filePath&&filePath.length()>0){
                final int length=filePath.length();
                final String fileDivider=File.separator;
                int lastNameIndex=filePath.lastIndexOf(fileDivider);
                if (lastNameIndex>=0&&lastNameIndex<length){
                    parent=filePath.substring(0,lastNameIndex);
                    lastNameIndex+=1;
                    if (lastNameIndex>=0&&lastNameIndex<length){
                        name=filePath.substring(lastNameIndex,length);
                        final String extensionDivider=".";
                        int lastExtensionIndex=filePath.lastIndexOf(extensionDivider,lastNameIndex);
                        if (lastExtensionIndex>=0&&lastExtensionIndex<length){
                            extension=filePath.substring(lastExtensionIndex);
                        }
                    }
                }
            }
           return new LocalPath(parent,name,extension);
        }
        return null;
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
