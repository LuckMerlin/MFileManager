package com.luckmerlin.file.binding;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.BindingList;
import com.luckmerlin.databinding.CustomBinding;
import com.luckmerlin.file.Thumb;

import java.io.File;

public class ImageBinding implements CustomBinding {
    private final Object mImage;
    private boolean mCircleCrop=false;
    private boolean mCenterCrop=false;
    private int mRoundedCorners;
    private boolean mCenterInside;

    private ImageBinding(Object image){
        mImage=image;
    }

    public ImageBinding circleCrop(boolean circleCrop){
        mCircleCrop=circleCrop;
        return this;
    }

    public ImageBinding centerCrop(boolean centerCrop){
        mCenterCrop=centerCrop;
        return this;
    }

    public ImageBinding centerInside(boolean centerInside){
        mCenterInside=centerInside;
        return this;
    }

    public ImageBinding roundedCorners(int roundedCorners){
        mRoundedCorners=roundedCorners;
        return this;
    }

    public static ImageBinding image(Object image){
        return new ImageBinding(image);
    }

    @Override
    public boolean onBind(View view) {
        if (null==view||!(view instanceof ImageView)){
            return false;
        }
        final ImageView imageView=(ImageView)view;
        Object image=mImage;
        if (null!=image){
            int roundedCorners=mRoundedCorners;
            RequestOptions options=new RequestOptions();
            options=mCircleCrop?options.circleCrop():options;
            options=mCenterInside?options.centerInside():options;
            options=mCenterCrop?options.centerCrop():options;
            options=roundedCorners>0?options.transform(new RoundedCorners(roundedCorners)):options;
            if (image instanceof String) {
                String path = (String) image;
                if (path.startsWith(File.separator)) {
                    image=new File(path);
                }else if (path.startsWith("http")){
                    return null!=Glide.with(imageView).load(path).apply(options).into(imageView);
                }
            }else if (image instanceof Thumb){
                image=((Thumb)image).getThumb();
            }
            if (image instanceof File){
                return null!=Glide.with(imageView).load((File)image).apply(options).into(imageView);
            }else if (image instanceof Drawable){
                return null!=Glide.with(imageView).load((Drawable)image).apply(options).into(imageView);
            }else if (image instanceof Integer){
                return null!=Glide.with(imageView).load((Integer)image).into(imageView);
            }else if (image instanceof Bitmap){
                return null!=Glide.with(imageView).load((Bitmap)image).apply(options).into(imageView);
            }else if (image instanceof byte[]){
                return null!=Glide.with(imageView).load((byte[]) image).apply(options).into(imageView);
            }
        }
        imageView.setImageDrawable(null);//Clean
        return false;
    }
}
