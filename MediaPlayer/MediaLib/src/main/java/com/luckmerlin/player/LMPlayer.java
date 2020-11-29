package com.luckmerlin.player;

import android.view.Surface;
import android.view.SurfaceHolder;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.media.AbstractPlayer;
import com.luckmerlin.media.Media;

public class LMPlayer extends AbstractPlayer {
    private Surface mDisplay;
    static {
        System.loadLibrary("LMPlayer");
    }

    @Override
    public boolean setDisplay(Surface holder, String debug) {
        mDisplay=holder;
        return true;
    }

    @Override
    protected long onPlay(Media media, double seek, String debug) {
        final String path=null!=media?media.getPath():null;
        if (null==path||path.length()<=0){
            Debug.W("Can't play media while path invalid "+(null!=debug?debug:"."));
            return INVALID;
        }
        Debug.D("Play media "+(null!=debug?debug:"."));
        return jniPlayMedia(path,seek,mDisplay,debug);
    }

    @Override
    public String getVersion() {
        return getJniVersion();
    }

    private native boolean jniIsCreate();
    private native boolean jniCreate(String debug);
    private native boolean jniDestroy(String debug);
    private native String getJniVersion();
    private native long jniPlayMedia(String path,double seek,Surface display,String debug);
}
