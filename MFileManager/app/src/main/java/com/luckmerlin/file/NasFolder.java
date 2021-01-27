package com.luckmerlin.file;

import com.luckmerlin.adapter.recycleview.Section;

import java.util.List;

public class NasFolder<A,T extends NasPath> extends Path implements Section<A,T> {
    private long from;
    private List<T> data;
    private long length;
    private T folder;
    private A mArg;

    @Override
    public String getParent() {
        T current=folder;
        return null!=current?current.getParent():null;
    }

    @Override
    public A getArg() {
        return mArg;
    }

    @Override
    public String getName() {
        T current=folder;
        return null!=current?current.getName():null;
    }

    @Override
    public String getExtension() {
        T current=folder;
        return null!=current?current.getExtension():null;
    }

    @Override
    public String getSep() {
        T current=folder;
        return null!=current?current.getSep():null;
    }

    @Override
    public String getMime() {
        T current=folder;
        return null!=current?current.getMime():null;
    }

    @Override
    public long getFrom() {
        return from;
    }

    @Override
    public long getModifyTime() {
        T current=folder;
        return null!=current?current.getModifyTime():0;
    }

    @Override
    public long getLength() {
        T current=folder;
        return null!=current?current.getLength():0;
    }

    @Override
    public boolean isDirectory() {
        T current=folder;
        return null!=current&&current.isDirectory();
    }

    @Override
    public int getPermission() {
        T current=folder;
        return null!=current?current.getPermission():PERMISSION_NONE;
    }


    @Override
    public List<T> getData() {
        return data;
    }

    @Override
    public long getTotal() {
        T current=folder;
        return null!=current?current.getTotal():0;
    }

    public T getFolder() {
        return folder;
    }
}
