package com.luckmerlin.file;

public class NasPath extends Path{
    private String name;
    private String parent;
    private String host;
    private int port;
    private long size;
    private long length;
    private long modifyTime;
    private String extension;
    private String pathSep;

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
    public long getLength() {
        return length;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean accessible() {
        return false;
    }

    @Override
    public long getTotal() {
        return size;
    }
}
