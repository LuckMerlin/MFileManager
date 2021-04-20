package com.luckmerlin.lib;

import java.io.Serializable;

public final class StringBuffer implements Appendable, CharSequence, Serializable {
    private final java.lang.StringBuffer mBuffer;

    public StringBuffer() {
        this(1);
    }

    public StringBuffer(int capacity) {
        mBuffer=new java.lang.StringBuffer(capacity);
    }

    public StringBuffer( String str) {
        this(1);
        append(str);
    }

    public StringBuffer(CharSequence seq) {
        this(1);
        append(seq);
    }

    @Override
    public StringBuffer append( CharSequence charSequence) {
        java.lang.StringBuffer buffer=mBuffer;
        if (null!=buffer&&charSequence!=null){
            buffer.append(charSequence);
        }
        return this;
    }

    @Override
    public StringBuffer append( CharSequence charSequence, int i, int i1) {
        java.lang.StringBuffer buffer=mBuffer;
        if (null!=buffer&&charSequence!=null){
            buffer.append(charSequence,i,i1);
        }
        return this;
    }

    @Override
    public StringBuffer append(char c) {
        java.lang.StringBuffer buffer=mBuffer;
        if (null!=buffer){
            buffer.append(c);
        }
        return this;
    }

    @Override
    public int length() {
        java.lang.StringBuffer buffer=mBuffer;
        return null!=buffer?buffer.length():-1;
    }

    @Override
    public char charAt(int i) {
        java.lang.StringBuffer buffer=mBuffer;
        return null!=buffer?buffer.charAt(i):0;
    }

    @Override
    public CharSequence subSequence(int i, int i1) {
        java.lang.StringBuffer buffer=mBuffer;
        return null!=buffer?buffer.subSequence(i,i1):null;
    }

    @Override
    public String toString() {
        java.lang.StringBuffer buffer=mBuffer;
        return null!=buffer?buffer.toString():super.toString();
    }
}
