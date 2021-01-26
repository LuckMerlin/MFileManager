//package com.luckmerlin.file.util;
//
//import android.content.Context;
//import android.content.pm.ApplicationInfo;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.graphics.drawable.Drawable;
//import android.os.Build;
//import android.view.View;
//import android.widget.ImageView;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.bumptech.glide.load.model.GlideUrl;
//import com.bumptech.glide.load.model.LazyHeaders;
//import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
//import com.bumptech.glide.request.RequestOptions;
//import com.merlin.api.Label;
//import com.merlin.api.Res;
//import com.merlin.bean.Path;
//import com.merlin.browser.FileDefaultThumb;
//import com.merlin.click.Clicker;
//import com.merlin.file.R;
//
//import java.io.File;
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//
//public class PathGlider {
//
//    public final boolean set(View view,Drawable image,boolean background){
//        if (null!=view){
//            if (background){
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    view.setBackground(image);
//                }else{
//                    view.setBackgroundDrawable(image);
//                }
//            }else if (view instanceof ImageView){
//                ((ImageView)view).setImageDrawable(image);
//            }
//            return true;
//        }
//        return false;
//    }
//
//    public final boolean glide(View view, Object object, boolean background){
//        if (null!=object&&null!=view){
//            set(view,null,background);
//            if (object instanceof Integer){
//                if (background){
//                    view.setBackgroundResource((Integer)object);
//                }else if (view instanceof ImageView){
//                    Clicker.putRes(view, new Res((Integer)object,null));
//                    ((ImageView)view).setImageResource((Integer)object);
//                }
//                return true;
//            }
//            if (object instanceof Drawable){
//                set(view,(Drawable)object,background);
//            }else if (object instanceof Path&&view instanceof ImageView){
//                Path path=(Path)object;
//                if (path.isDirectory()){
//                    ((ImageView)view).setImageResource(R.drawable.hidisk_icon_folder);
//                    return false;
//                }
//                String mime=path.getMime();
//                final Integer iconDefId=(null != mime && mime.length() >0)?new FileDefaultThumb().thumb(mime):null;
//                if (null!=mime&&mime.length()>0){
//                    String pathValue=path.getPath();
//                    if (null!=pathValue&&pathValue.length()>0){
//                        if (mime.endsWith("application/vnd.android.package-archive")) {
//                            if (path.isLocal()){
//                                Context context=view.getContext();
//                                PackageManager manager = null!=context?context.getPackageManager():null;
//                                PackageInfo packageInfo = null!=manager?manager.getPackageArchiveInfo(pathValue, PackageManager.GET_ACTIVITIES):null;
//                                if (packageInfo != null) {
//                                    ApplicationInfo info = packageInfo.applicationInfo;
//                                    info.sourceDir = pathValue;
//                                    info.publicSourceDir = pathValue;
//                                    try {
//                                        ((ImageView)view).setImageDrawable(info.loadIcon(manager));
//                                        return true;
//                                    } catch (Exception e) {
//                                        //Do nothing
//                                    }
//                                }
//                            }else{
//                                return loadCloudFileThumb((ImageView)view, path,iconDefId);
//                            }
//                        }else if (mime.startsWith("image/")||mime.startsWith("video/")){
//                              if (path.isLocal()){
//                                  File localFile=null!=pathValue&&pathValue.length()>0?new File(pathValue):null;
//                                  if (null!=localFile){
//                                      Glide.with(view).load(localFile).into((ImageView)view);
//                                      return true;
//                                  }
//                              }else{
//                                  return loadCloudFileThumb((ImageView)view, path,iconDefId);
//                              }
//                        }
//                    }
//                }
//                ((ImageView)view).setImageResource(null!=iconDefId?iconDefId:R.drawable.hidisk_icon_unknown);
//                return true;
//            }
//            return false;
//        }
//        return false;
//    }
//
//    private boolean loadCloudFileThumb(ImageView view, Path path,Integer iconId){
//        String hostUri=null!=path&&null!=view?path.getHostUri():null;
//        if (null!=hostUri&&hostUri.length()>0){
//            String filePath=path.getPath();
//            try {
//                int width = view.getWidth();
//                int height = view.getHeight();
//                GlideUrl glideUrl = new GlideUrl(hostUri, new LazyHeaders.Builder().addHeader(Label.LABEL_THUMB,Boolean.toString(true))
//                        .addHeader(Label.LABEL_PATH, URLEncoder.encode(filePath,"utf-8")).addHeader(Label.LABEL_WIDTH,
//                        Integer.toString(width<=0?50:width)).addHeader(Label.LABEL_HEIGHT, Integer.toString(height<=0?50:height)).build());
//                if (null != glideUrl) {
//                    RoundedCorners roundedCorners = new RoundedCorners(1);
//                    RequestOptions options = RequestOptions.bitmapTransform(roundedCorners).override(width, height);
//                    Glide.with(view.getContext()).load(glideUrl).diskCacheStrategy(DiskCacheStrategy.NONE).
//                            centerCrop().apply(options).thumbnail(1f).error(null!=iconId?iconId:R.drawable.hidisk_icon_unknown).into(view);
//                }
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
//        return false;
//    }
//}
