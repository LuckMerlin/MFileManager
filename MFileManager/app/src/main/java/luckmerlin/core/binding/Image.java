package luckmerlin.core.binding;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import androidx.databinding.BindingAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.io.File;

/**
 * Create LuckMerlin
 * Date 11:36 2021/4/21
 * TODO
 */
public class Image {
    private boolean mCircleCrop=false;
    private boolean mCenterCrop=false;
    private boolean mCenterInside=false;
    private boolean mFitCenter=false;
    private Integer mPlaceHolder=null;
    private Object mImage;

    public final Image image(Object image){
        mImage=image;
        return this;
    }

    public final Image circleCrop(boolean enable){
        mCircleCrop=enable;
        return this;
    }

    public final Image centerCrop(boolean enable){
        mCenterCrop=enable;
        return this;
    }

    public final Image centerInside(boolean enable){
        mCenterInside=enable;
        return this;
    }

    public final Image fitCenter(boolean enable){
        mFitCenter=enable;
        return this;
    }

    public final Image placeHolder(Integer resId){
        mPlaceHolder=resId;
        return this;
    }

    public static Image img(Object image){
        return new Image().image(image);
    }

    private boolean setImage(ImageView imageView,Object imageObj,RequestOptions options){
        if (null!=imageView){
            Context context=imageView.getContext();
            if (null!=imageObj){
                if (imageObj instanceof File){
                    return null!=Glide.with(context).load((File)imageObj). apply(options).into(imageView)||true;
                }else if (imageObj instanceof Bitmap){
                    return null!=Glide.with(context).load((Bitmap)imageObj). apply(options).into(imageView)||true;
                }else if (imageObj instanceof Drawable){
                    return null!=Glide.with(context).load((Drawable)imageObj). apply(options).into(imageView)||true;
                }else if (imageObj instanceof Uri){
                    return null!=Glide.with(context).load((Uri)imageObj). apply(options).into(imageView)||true;
                }else if (imageObj instanceof Integer){
                    return null!=Glide.with(context).load((Integer)imageObj). apply(options).into(imageView)||true;
                }else if (imageObj instanceof String){
                    String path=(String)imageObj;
                    if (path.length()>0){
                        if (path.trim().startsWith("http")){
                            return null!=Glide.with(context).load(Uri.parse(path)). apply(options).into(imageView)||true;
                        }else{
                            return setImage(imageView,new File(path),options);
                        }
                    }
                }
            }
            imageView.setImageDrawable(null);
            return true;
        }
        return false;
    }

    @BindingAdapter("image")
    public static void setImage(ImageView imageView, Object imageObj){
        if (null!=imageView){
            if (null!=imageObj&&imageObj instanceof Image){
                Image image=(Image)imageObj;
                RequestOptions options = new RequestOptions();
                options=image.mCircleCrop?options.circleCrop():options;
                options=image.mCenterCrop?options.centerCrop():options;
                options=image.mCenterInside?options.centerInside():options;
                options=image.mFitCenter?options.fitCenter():options;
                Integer placeHolder=image.mPlaceHolder;
                options=null!=placeHolder?options.placeholder(placeHolder):options;
                image.setImage(imageView, image.mImage,options);
            }else{
                new Image().setImage(imageView, imageObj, null);
            }
        }
    }
}
