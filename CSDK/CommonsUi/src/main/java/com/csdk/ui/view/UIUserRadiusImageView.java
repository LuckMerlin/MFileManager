package com.csdk.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.csdk.api.bean.User;
import com.csdk.ui.R;

/**
 * Create LuckMerlin
 * Date 20:15 2020/9/3
 * TODO
 */
public class UIUserRadiusImageView extends ImageView {
    private Boolean mFemale=null;
    private String mAvatarUrl;

    public UIUserRadiusImageView(Context context) {
        this(context,null);
    }

    public UIUserRadiusImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public UIUserRadiusImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(ScaleType.FIT_XY);
        loadUserData();
    }

    public final void setFemale(boolean female){
        mFemale=female;
        loadUserData();
    }

    @BindingAdapter("useFemale")
    public static void setUserFemale(UIUserRadiusImageView view, boolean female){
        if (null!=view) {
            view.setFemale(female);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return isEnabled()?super.onTouchEvent(event):true;
    }

    @BindingAdapter("avatarUrl")
    public static void setUserAvatarUrl(UIUserRadiusImageView view, String avatarUrl){
        if (null!=view) {
            view.setUserAvatarUrl(avatarUrl);
        }
    }

    public final void setUser(User user){
        mFemale=null!=user?user.isFemale():null;
        mAvatarUrl=null!=user?user.getAvatarUrl():null;
        loadUserData();
    }

    public final void setUserAvatarUrl(String avatarUrl){
        mAvatarUrl=avatarUrl;
        loadUserData();
    }

    private final void loadUserData( ) {
        String avatarUrl=mAvatarUrl;
        Boolean female=mFemale;
        int defResId=null!=female?female? R.drawable.csdk_user_default_logo_female: R.drawable.
                csdk_user_default_logo_man: R.drawable.csdk_user_default_logo_man;
        RequestOptions options = new RequestOptions().circleCrop().centerCrop().placeholder(
                null!=female&&female? R.drawable.csdk_user_default_logo_female: R.drawable.csdk_user_default_logo_man);
        if (null != avatarUrl && avatarUrl.length() > 0) {
            Glide.with(getContext()).load(avatarUrl).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).
                    apply(options).transform(new CircleCrop()).into(this);
        } else {
            Glide.with(getContext()).load(defResId).apply(options).transform(new CircleCrop()).into(this);
        }
    }

}
