package com.csdk.api.struct;

import android.text.style.ClickableSpan;

/**
 * Create LuckMerlin
 * Date 17:00 2021/1/25
 * TODO
 */
public abstract class StructClickableSpan extends ClickableSpan {
    private final int mStart;
    private final int mEnd;

    public StructClickableSpan(int start,int end){
        mStart=start;
        mEnd=end;
    }

    public int getEnd() {
        return mEnd;
    }

    public int getStart() {
        return mStart;
    }

}
