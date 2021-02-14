package com.csdk.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.csdk.api.core.Debug;

/**
 * Create LuckMerlin
 * Date 17:00 2020/8/7
 * TODO
 */
public class GlideImageView extends SquareImageView {

    public GlideImageView(Context context) {
        this(context, null);
    }

    public GlideImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GlideImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void setImage(Object image){
        if (null!=image){//Must check If not NULL
            if (image instanceof CharSequence){
                String value=((CharSequence)image).toString();
                Uri uri= null!=value&&value.startsWith("http")? Uri.parse(value):null;
                if (null!=uri){
                    Glide.with(this).load(uri).into(this);
                }else{
                    Glide.with(this).load(value).into(this);
                }
            }else if (image instanceof Integer&&((Integer)image)!=0){
                Glide.with(this).load((Integer)image).into(this);
            }else if (image instanceof Drawable){
                Glide.with(this).load((Drawable)image).into(this);
            }else if (image instanceof Bitmap){
                Glide.with(this).load((Bitmap)image).into(this);
            }
        }else{
            setImageDrawable(null);
        }
    }

    @BindingAdapter("image")
    public static void setImage(GlideImageView view, Object imageObj){
        if (null!=view) {
            view.setImage(imageObj);
        }
    }



}
