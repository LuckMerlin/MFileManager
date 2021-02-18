package com.csdk.api.struct;

import android.graphics.Color;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import java.util.ArrayList;

public final class StructArrayList extends ArrayList<Struct> implements CharSequence{
    private int mSelectStart;
    private int mSelectEnd;

    @Override
    public int length() {
        CharSequence charSequence=toText();
        return null!=charSequence?charSequence.length():0;
    }

    @Override
    public char charAt(int i) {
        CharSequence charSequence=toText();
        return null!=charSequence&&i>=0&&i<charSequence.length()?charSequence.charAt(i):'0';
    }

    public StructArrayList setSelectEnd(int selectEnd) {
        this.mSelectEnd = selectEnd;
        return this;
    }

    public final Struct structAt(int index){
        if (index>=0){
            index+=1;
            int total=0;
            for (Struct struct:this) {
                if (null!=struct){
                    int length=struct.length();
                    if ((total+=(length<=0?0:length))>=index){
                        return struct;
                    }
                }
            }
        }
        return null;
    }

    public StructArrayList setSelectStart(int selectStart) {
        this.mSelectStart = selectStart;
        return this;
    }

    @Override
    public CharSequence subSequence(int i, int i1) {
        StructSpannableStringBuilder charSequence=toText();
        int length=null!=charSequence?charSequence.length():-1;
        return length>0&&i>=0&&i1>i?charSequence.subSequence(i,Math.min(length,i1)):null;
    }

    public StructSpannableStringBuilder toText(){
        return toText(null);
    }

    public StructSpannableStringBuilder toText(OnStructClickListener callback){
        StructSpannableStringBuilder textBuilder=new StructSpannableStringBuilder("",this);
        for (Struct struct:this) {
            if (null==struct){
                continue;
            }
            final CharSequence charSequence=struct.toText();
            if (null!=charSequence&&charSequence.length()>0){
                final int start=textBuilder.length();
                textBuilder.append(charSequence);
                final int end=textBuilder.length();
                if (struct.isAnyType(Struct.TYPE_LINK)){
                    Integer titleColor=struct.getTitleColor();
                    textBuilder.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            if (null!=callback){
                                callback.onStructClick(widget,struct,charSequence,start,end);
                            }
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setUnderlineText(false);
                            if (null!=titleColor&&null!=ds){
                                ds.setColor(Color.RED);
                            }
                        }
                    }, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return textBuilder;
    }

    public int getSelectEnd() {
        return mSelectEnd;
    }

    public int getSelectStart() {
        return mSelectStart;
    }

    @Override
    public String toString() {
        CharSequence text=toText();
        return null!=text?text.toString():super.toString();
    }
}
