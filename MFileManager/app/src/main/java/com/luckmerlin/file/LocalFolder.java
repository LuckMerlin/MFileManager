package com.luckmerlin.file;

import java.util.List;

public class LocalFolder<A> extends Folder<A,LocalPath> {

    @Override
    public List<LocalPath> getData() {
        return null;
    }

    @Override
    public A getArg() {
        return null;
    }

    @Override
    public long getFrom() {
        return 0;
    }

    @Override
    public String getParent() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getExtension() {
        return null;
    }

    @Override
    public String getSep() {
        return null;
    }

    @Override
    public long getModifyTime() {
        return 0;
    }

    @Override
    public long getLength() {
        return 0;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public long getTotal() {
        return 0;
    }

    @Override
    public int getPermission() {
        return 0;
    }

    @Override
    public String getMime() {
        return null;
    }
}
