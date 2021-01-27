package com.luckmerlin.file.binding;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.luckmerlin.databinding.CustomBinding;
import com.luckmerlin.file.FileDefaultThumb;
import com.luckmerlin.file.LocalPath;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.R;

import java.io.File;

public class ImageBinding implements CustomBinding {
    private final Path mPath;

    private ImageBinding(Path path){
        mPath=path;
    }

    public static ImageBinding image(Path path){
        return new ImageBinding(path);
    }

    @Override
    public boolean onBind(View view) {
        if (null==view||!(view instanceof ImageView)){
            return false;
        }
        final ImageView imageView=(ImageView)view;
        Path path=mPath;
        if (null!=path){
            if (path.isDirectory()){
                imageView.setImageResource(R.drawable.hidisk_icon_folder);
                return false;
            }
            String mime=path.getMime();
            if (null== mime ||mime.length() <=0){
                imageView.setImageResource(R.drawable.hidisk_icon_unknown);
                return false;
            }
            String pathValue = path.getPath();
            if (path instanceof LocalPath){
                if (mime.endsWith("application/vnd.android.package-archive")) {
                    Context context=view.getContext();
                    imageView.setImageDrawable(null!=pathValue?loadLocalApkIcon(context,pathValue):null);
                    return true;
                }else if (mime.startsWith("image/")||mime.startsWith("video/")){
                    File localFile=null!=pathValue?new File(pathValue):null;
                    if (null!=localFile){
                        Glide.with(view).load(localFile).into((ImageView)view);
                        return true;
                    }
                    imageView.setImageDrawable(null);
                    return true;
                }
            }else{//Cloud file

            }
            final Integer iconDefId=new FileDefaultThumb().thumb(mime);
            if (null!=iconDefId){
                imageView.setImageResource(iconDefId);
                return true;
            }
            imageView.setImageDrawable(null);
            return true;
        }
        return false;
    }

    private Drawable loadLocalApkIcon(Context context,String pathValue){
        PackageManager manager = null!=context&&null!=pathValue?context.getPackageManager():null;
        PackageInfo packageInfo = null!=manager?manager.getPackageArchiveInfo(pathValue, PackageManager.GET_ACTIVITIES):null;
        if (packageInfo != null){
            try {
                ApplicationInfo info = packageInfo.applicationInfo;
                info.sourceDir = pathValue;
                info.publicSourceDir = pathValue;
                return info.loadIcon(manager);
            } catch (Exception e) {
                //Do nothing
            }
        }
        return null;
    }
}
