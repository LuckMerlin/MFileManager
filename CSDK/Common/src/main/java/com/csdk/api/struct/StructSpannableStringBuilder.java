package com.csdk.api.struct;

import android.text.SpannableStringBuilder;

public final class StructSpannableStringBuilder extends SpannableStringBuilder {
    private final StructArrayList mStructArrayList;

    public StructSpannableStringBuilder(CharSequence text,StructArrayList list) {
        super(text);
        mStructArrayList=list;
    }

    public StructSpannableStringBuilder(CharSequence text,int start,int end,StructArrayList list) {
        super(text,start,end);
        mStructArrayList=list;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return new StructSpannableStringBuilder(this,start, end,mStructArrayList);
    }

    public StructArrayList getStructArrayList() {
        return mStructArrayList;
    }
}
