package com.luckmerlin.file.binding;

import android.content.res.ColorStateList;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.CustomBinding;
import com.luckmerlin.file.Folder;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.ui.OnPathSpanClick;

import java.lang.ref.WeakReference;

public final class PathTextViewBinding implements CustomBinding {
    private final Folder mPath;
    private WeakReference<OnPathSpanClick> mReference;

    private PathTextViewBinding(Folder path,OnPathSpanClick callback){
        mPath=path;
        mReference=null!=callback?new WeakReference<>(callback):null;
    }

    public static PathTextViewBinding bind(Folder path, OnPathSpanClick callback){
        return new PathTextViewBinding(path,callback);
    }

    @Override
    public boolean onBind(View view) {
        if (null!=view&&view instanceof TextView){
            CharSequence charSequence=null;
            TextView textView=(TextView)view;
            Folder path=mPath;
            if (null!=path){
                String pathValue=path.getPath();
                String pathSep=path.getSep();
                if (null!=pathSep&&pathSep.length()>0&&null!=pathValue){
                    SpannableStringBuilder builder = new SpannableStringBuilder("");
                    String[] splits=pathValue.split(pathSep);
                    int splitLength=null!=splits?splits.length:-1;
                    int startIndex=0;String child;int end=0;
                    WeakReference<OnPathSpanClick> reference=mReference;
                    for (int i = 0; i < splitLength; i++) {
                        if (null!=(child=splits[i])&&child.length()>0){
                            builder.append(pathSep);
                            builder.append(child);
                            end=(startIndex+=1)+child.length();
                            builder.setSpan(new TextViewClickableSpan(path,startIndex,end,builder.toString()){
                                @Override
                                public void onClick(View view) {
                                    OnPathSpanClick callback= null!=reference?reference.get():null;
                                    if (null!=callback){
                                        callback.onPathSpanClick(mPath,mStart,mEnd,mValue);
                                    }else{
                                        mReference=null;
                                    }
                                }

                                @Override
                                public void updateDrawState(@NonNull TextPaint textPaint) {
                                    super.updateDrawState(textPaint);
                                    if (null != textPaint) {
                                        textPaint.setUnderlineText(false);
                                        ColorStateList colorStateList = textView.getTextColors();
                                        if (null != colorStateList) {
                                            textPaint.setColor(colorStateList.getDefaultColor());
                                        }
                                    }
                                }
                            },startIndex,end,Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                            startIndex=end;
                        }
                    }
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                    charSequence=builder;
                }
            }
            textView.setText(null!=charSequence?charSequence:"");
            return true;
        }
        return false;
    }

    private static abstract class TextViewClickableSpan extends ClickableSpan {
        public final Path mPath;
        public final int mStart;
        public final int mEnd;
        public final String mValue;

        private TextViewClickableSpan(Path path,int start,int end,String value){
            mPath=path;
            mStart=start;
            mEnd=end;
            mValue=value;
        }
    }
}
