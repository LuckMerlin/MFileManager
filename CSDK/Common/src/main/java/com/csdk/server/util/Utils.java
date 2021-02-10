package com.csdk.server.util;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class Utils {

    /**
     * 记录第三方登录配置的key
     */
    private static Map<String, Object> THIRD_CONFIG = new HashMap<String, Object>();

    private static Map<String, Typeface> cachedFontMap = new HashMap<String, Typeface>();

    /**
     * 设备同步状态： 0-已经同步过基本信息， -1-已经注册过设备
     */
    private static final String DEVICESYN = "devicesyn";
    private static final String PREFS_DEVICE_ID = "device_id";
    private static final Object LOCKER = new Object();

    public static int pxToSp(final Context context, final float px) {
        return Math.round(px / context.getResources().getDisplayMetrics().scaledDensity);
    }

    public static int spToPx(final Context context, final float sp) {
        return Math.round(sp * context.getResources().getDisplayMetrics().scaledDensity);
    }

    public static Typeface findFont(Context context, String fontPath, String defaultFontPath) {

        if (fontPath == null) {
            return Typeface.DEFAULT;
        }

        String fontName = new File(fontPath).getName();
        String defaultFontName = "";
        if (!TextUtils.isEmpty(defaultFontPath)) {
            defaultFontName = new File(defaultFontPath).getName();
        }

        if (cachedFontMap.containsKey(fontName)) {
            return cachedFontMap.get(fontName);
        } else {
            try {
                AssetManager assets = context.getResources().getAssets();

                if (Arrays.asList(assets.list("")).contains(fontPath)) {
                    Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontName);
                    cachedFontMap.put(fontName, typeface);
                    return typeface;
                } else if (Arrays.asList(assets.list("fonts")).contains(fontName)) {
                    Typeface typeface = Typeface.createFromAsset(context.getAssets(), String.format("fonts/%s", fontName));
                    cachedFontMap.put(fontName, typeface);
                    return typeface;
                } else if (Arrays.asList(assets.list("iconfonts")).contains(fontName)) {
                    Typeface typeface = Typeface.createFromAsset(context.getAssets(), String.format("iconfonts/%s", fontName));
                    cachedFontMap.put(fontName, typeface);
                    return typeface;
                } else if (!TextUtils.isEmpty(defaultFontPath) && Arrays.asList(assets.list("")).contains(defaultFontPath)) {
                    Typeface typeface = Typeface.createFromAsset(context.getAssets(), defaultFontPath);
                    cachedFontMap.put(defaultFontName, typeface);
                    return typeface;
                } else {
                    throw new Exception("Font not Found");
                }

            } catch (Exception e) {
                return Typeface.DEFAULT;
            }
        }

    }

    public static Map<String, Object> getThirdConfig() {
        return THIRD_CONFIG;
    }

    public static void putThirdExtra(String key, Object value) {
        THIRD_CONFIG.put(key, value);
    }

    /**
     * 获取包名
     *
     * @param context
     * @return
     */
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    /**
     * 获取宽度
     *
     * @param context
     * @return
     */
    public static int getWidth(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    /**
     * 获取高度
     *
     * @param context
     * @return
     */
    public static int getHeight(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    /**
     * 获取系统语言
     *
     * @param mContext
     * @return
     */
    public static String getLanguage(Context mContext) {
        Locale locale = mContext.getResources().getConfiguration().locale;
        if (locale == null)
            locale = Locale.getDefault();
        String language = locale.getLanguage();
        String country = locale.getCountry();
        if (!TextUtils.isEmpty(country))
            language += "_" + country;
        return language;
    }

    /**
     * 获取手机IMEI号
     */
    public static String getIMEI(Context context) {
        try {
            if (!checkPermission(context, Manifest.permission.READ_PHONE_STATE))
                return null;
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            return imei;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 读取手机唯一标识
     *
     * @param context
     * @return
     */
    public static String getIMSI(Context context) {
        try {
            if (!checkPermission(context, Manifest.permission.READ_PHONE_STATE))
                return null;
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String imsi = tm.getSubscriberId();
            return imsi;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检查是否有权限， true表示有权限， false表示失败或无权限
     *
     * @param ctx
     * @param permissionName
     * @return
     */
    public static boolean checkPermission(Context ctx, String permissionName) {
        try {
            if (PackageManager.PERMISSION_GRANTED == ctx.getPackageManager()
                    .checkPermission(permissionName, ctx.getPackageName())) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取设备ID
     *
     * @param context 环境上下文
     * @return 设备ID号
     */
    public static String getDeviceID(Context context) {
        if (null==context){
            return null;
        }
        UUID uuid = null;
        synchronized (LOCKER) {
            final SharedPreferences prefs = context.getSharedPreferences(DEVICESYN, 0);
            if (null==prefs){
                return null;
            }
            final String id = prefs.getString(PREFS_DEVICE_ID, null);

            if (id != null) {
                // Use the ids previously computed and stored in the prefs file
                uuid = UUID.fromString(id);
            } else {
                ContentResolver resolver=context.getContentResolver();
                if (null==resolver){
                    return null;
                }
                final String androidId = Settings.Secure.getString(resolver, Settings.Secure.ANDROID_ID);
                // Use the Android ID unless it's broken, in which case fallback on deviceId,
                // unless it's not available, then fallback on a random number which we store
                // to a prefs file
                try {
                    if (!"9774d56d682e549c".equals(androidId)) {
                        uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                    } else {
                        if (!checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                            uuid = UUID.randomUUID();
                        } else {
                            String deviceId;
                            try {
                                deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                            } catch (Exception e) {
                                deviceId = null;
                            }
                            uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                   //Do nothing
                }
                // Write the value out to the prefs file
                Editor editor=prefs.edit();
                if (null!=editor){
                    editor.putString(PREFS_DEVICE_ID, uuid.toString()).commit();
                }
            }
        }
        return null!=uuid?uuid.toString():null;
    }

    /**
     * 版本名
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        PackageInfo pi = getPackageInfo(context);
        if (pi != null) {
            return getPackageInfo(context).versionName;
        }
        return "";
    }

    /**
     * 版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageInfo pi = getPackageInfo(context);
        if (pi != null) {
            return getPackageInfo(context).versionCode;
        }
        return 0;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    /**
     * 获取系统语言
     *
     * @param mContext
     * @return
     */
    public static String getSysLanguage(Context mContext) {
        Locale locale = mContext.getResources().getConfiguration().locale;
        if (locale == null)
            locale = Locale.getDefault();
        String language = locale.getLanguage();
        return language;
    }

    /**
     * 判断用户是否快速点击了按钮
     */
    private static long lastClickTime;

    public static boolean isFastDoubleClick(int T) {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < T * 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static void openBrowser(Context context, String url) {
        try {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取应用程序名称
     */
    public static synchronized String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageManager.getApplicationLabel(packageInfo.applicationInfo).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取横竖屏
     *
     * @param context
     * @return
     */
    public static int getOrientation(Context context) {
        try {
            //获取设置的配置信息
            Configuration mConfiguration = context.getResources().getConfiguration();
            //获取屏幕方向
            return mConfiguration.orientation;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
