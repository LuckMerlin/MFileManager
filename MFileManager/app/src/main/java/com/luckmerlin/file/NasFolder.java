package com.luckmerlin.file;

import java.util.List;

public class NasFolder<A> extends Folder<A,NasPath> {
    private long from;
    private List<NasPath> data;
    private long length;
    private NasPath folder;
    private A mArg;
    private String mHost;
    private int mPort;

    @Override
    public String getParent() {
        NasPath current=folder;
        return null!=current?current.getParent():null;
    }

    public final NasFolder setHost(String host) {
        this.mHost = host;
        return this;
    }

    public final NasFolder setPort(int port) {
        this.mPort = port;
        return this;
    }

    public final String getHost() {
        return mHost;
    }

    public final NasFolder setFolder(NasPath folder) {
        this.folder = folder;
        return this;
    }

    public final String getHostUrl() {
        return mHost;
    }


    public final int getPort() {
        return mPort;
    }

    @Override
    public A getArg() {
        return mArg;
    }

    @Override
    public String getName() {
        NasPath current=folder;
        return null!=current?current.getName():null;
    }

    @Override
    public String getExtension() {
        NasPath current=folder;
        return null!=current?current.getExtension():null;
    }

    @Override
    public String getSep() {
        NasPath current=folder;
        return null!=current?current.getSep():null;
    }

    @Override
    public String getMime() {
        NasPath current=folder;
        return null!=current?current.getMime():null;
    }

    @Override
    public long getFrom() {
        return from;
    }

    @Override
    public long getModifyTime() {
        NasPath current=folder;
        return null!=current?current.getModifyTime():0;
    }

    @Override
    public long getLength() {
        NasPath current=folder;
        return null!=current?current.getLength():0;
    }

    @Override
    public boolean isDirectory() {
        NasPath current=folder;
        return null!=current&&current.isDirectory();
    }

    @Override
    public int getPermission() {
        NasPath current=folder;
        return null!=current?current.getPermission():PERMISSION_NONE;
    }


    @Override
    public List<NasPath> getData() {
        return data;
    }

    @Override
    public long getTotal() {
        NasPath current=folder;
        return null!=current?current.getTotal():0;
    }

    public NasPath getFolder() {
        return folder;
    }
}
