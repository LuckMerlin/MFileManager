package com.luckmerlin.file.binding;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.luckmerlin.databinding.CustomBinding;
import java.io.File;

public class ImageBinding implements CustomBinding {
    private final Object mImage;

    private ImageBinding(Object image){
        mImage=image;
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
            if (image instanceof String) {
                String path = (String) image;
                if (path.startsWith(File.separator)) {
                    return null!=Glide.with(imageView).load(new File(path)).into(imageView);
                }else if (path.startsWith("http")){
                    return null!=Glide.with(imageView).load(path).into(imageView);
                }
            }else if (image instanceof Drawable){
                imageView.setImageDrawable((Drawable)image);
                return true;
            }else if (image instanceof Integer){
                try {
                    Drawable drawable=imageView.getResources().getDrawable((Integer)image);
                    imageView.setImageDrawable(drawable);
                    return true;
                }catch (Exception e){
                    //Do nothing
                }
            }else if (image instanceof Bitmap){
                imageView.setImageBitmap((Bitmap)image);
                return true;
            }
        }
        imageView.setImageDrawable(null);//Clean
        return false;
    }
}
