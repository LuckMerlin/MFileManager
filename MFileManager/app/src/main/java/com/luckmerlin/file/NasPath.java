package com.luckmerlin.file;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

public final class NasPath extends Path implements Parcelable {
    private String name;
    private String parent;
    private String host;
    private int port;
    private long size;
    private long length;
    private long modifyTime;
    private String thumb;
    private String extension;
    private String mime;
    private String pathSep;
    private int permissions;
    private boolean link;
    private String md5;

    private NasPath(Parcel in) {
        name = in.readString();
        parent = in.readString();
        host = in.readString();
        port = in.readInt();
        size = in.readLong();
        length = in.readLong();
        modifyTime = in.readLong();
        thumb = in.readString();
        extension = in.readString();
        mime = in.readString();
        pathSep = in.readString();
        permissions = in.readInt();
        link = in.readByte() != 0;
        md5 = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(parent);
        dest.writeString(host);
        dest.writeInt(port);
        dest.writeLong(size);
        dest.writeLong(length);
        dest.writeLong(modifyTime);
        dest.writeString(thumb);
        dest.writeString(extension);
        dest.writeString(mime);
        dest.writeString(pathSep);
        dest.writeInt(permissions);
        dest.writeByte((byte) (link ? 1 : 0));
        dest.writeString(md5);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NasPath> CREATOR = new Creator<NasPath>() {
        @Override
        public NasPath createFromParcel(Parcel in) {
            return new NasPath(in);
        }

        @Override
        public NasPath[] newArray(int size) {
            return new NasPath[size];
        }
    };

    public NasPath setExtension(String extension) {
        this.extension = extension;
        return this;
    }

    public NasPath setHost(String host) {
        this.host = host;
        return this;
    }

    public NasPath setLength(long length) {
        this.length = length;
        return this;
    }

    public NasPath setMime(String mime) {
        this.mime = mime;
        return this;
    }

    @Override
    public final boolean isLink() {
        return link;
    }

    @Override
    public Object getThumb() {
        String thumb=this.thumb;
        return null!=thumb&&thumb.length()>0?Base64.decode(thumb.getBytes(), Base64.DEFAULT):null;
    }

    public NasPath setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
        return this;
    }

    public NasPath setName(String name) {
        this.name = name;
        return this;
    }

    public NasPath setParent(String parent) {
        this.parent = parent;
        return this;
    }

    public NasPath setPathSep(String pathSep) {
        this.pathSep = pathSep;
        return this;
    }

    public NasPath setPermissions(int permissions) {
        this.permissions = permissions;
        return this;
    }

    @Override
    public String getMd5() {
        return md5;
    }

    public NasPath setPort(int port) {
        this.port = port;
        return this;
    }

    public NasPath setSize(long size) {
        this.size = size;
        return this;
    }

    public final String getHostPort(){
        String host=this.host;
        return null!=host?host+":"+port:null;
    }

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

    @Override
    public boolean equals(Object o) {
        if (super.equals(o)&&null!=o&&o instanceof NasPath&&((NasPath)o).port==port){
            return isStringEqual(host,((NasPath)o).host);
        }
        return super.equals(o);
    }
}
