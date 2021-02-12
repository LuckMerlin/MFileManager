package com.csdk.ui.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.csdk.debug.Logger;

public class ScaleUtils {

    private static int mScaleWidth = 0;
    private static int mScaleHeight = 0;

    public static void initScaleParams(Context context) {
        try {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            mScaleWidth = metrics.widthPixels;
            mScaleHeight = metrics.heightPixels;
            Logger.D("initScaleParams width1:" + mScaleWidth + ",height1:" + mScaleHeight);
            if (mScaleWidth * 9 > mScaleHeight * 16) {
                mScaleWidth = mScaleHeight * 16 / 9;
            } else {
                mScaleHeight = mScaleWidth * 9 / 16;
            }
            Logger.D("initScaleParams width2:" + mScaleWidth + ",height2:" + mScaleHeight);
        } catch (Exception e) {
            Logger.D("initScaleParams e:" + e.getMessage());
        }
    }

    public static int getScaleWidth() {
        return mScaleWidth;
    }

    public static int getScaleHeight() {
        return mScaleHeight;
    }
}
