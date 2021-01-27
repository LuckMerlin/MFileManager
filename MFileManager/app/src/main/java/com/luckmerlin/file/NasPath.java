package com.luckmerlin.file;

public final class NasPath extends Path{
    private String name;
    private String parent;
    private String host;
    private int port;
    private long size;
    private long length;
    private long modifyTime;
    private String extension;
    private String mime;
    private String pathSep;
    private int permissions;

    @Override
    public String getParent() {
        return parent;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getExtension() {
        return extension;
    }

    @Override
    public String getSep() {
        return pathSep;
    }

    @Override
    public long getModifyTime() {
        return modifyTime;
    }

    @Override
    public String getMime() {
        return mime;
    }

    @Override
    public long getLength() {
        return length;
    }

    @Override
    public int getPermission() {
        return permissions;
    }

    @Override
    public boolean isDirectory() {
        return size>=0;
    }

    @Override
    public long getTotal() {
        return size;
    }
}
