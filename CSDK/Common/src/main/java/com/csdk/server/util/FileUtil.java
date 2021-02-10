package com.csdk.server.util;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理录音文件的类
 *
 * @author chenmy0709
 * @version V001R001C01B001
 * @deprecated
 */
public class FileUtil {

    private static String rootPath = "hImSdk";
    //原始文件(不能播放)
    private final static String AUDIO_PCM_BASEPATH = File.separator + rootPath + File.separator + "pcm" + File.separator;
    //可播放的高质量音频文件
    private final static String AUDIO_WAV_BASEPATH =  File.separator + rootPath + File.separator + "wav" + File.separator;

    private static void setRootPath(String rootPath) {
        FileUtil.rootPath = rootPath;
    }

    public static String getPcmFileAbsolutePath(String fileName) {
        return getFileAbsolutePath(fileName,".pcm");
    }

    public static String getFileAbsolutePath(String fileName,String postfix) {
        try {
            if (TextUtils.isEmpty(fileName)) {
                throw new NullPointerException("fileName isEmpty");
            }
            if (!isSdcardExit()) {
                throw new IllegalStateException("sd card no found");
            }
            String mAudioRawPath = "";
            if (isSdcardExit()) {
                if (null!=postfix&&postfix.length()>0){
                    if (!fileName.endsWith(postfix)) {
                        fileName = fileName + postfix;
                    }
                }
                String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_PCM_BASEPATH;

                File file = new File(fileBasePath);
                //创建目录
                if (!file.exists()) {
                    file.mkdirs();
                }

                mAudioRawPath = fileBasePath + fileName;
            }

            return mAudioRawPath;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getWavFileAbsolutePath(String fileName) {
        if (fileName == null) {
            throw new NullPointerException("fileName can't be null");
        }
        if (!isSdcardExit()) {
            throw new IllegalStateException("sd card no found");
        }

        String mAudioWavPath = "";
        if (isSdcardExit()) {
            if (!fileName.endsWith(".wav")) {
                fileName = fileName + ".wav";
            }
            String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH;
            File file = new File(fileBasePath);
            //创建目录
            if (!file.exists()) {
                file.mkdirs();
            }
            mAudioWavPath = fileBasePath + fileName;
        }
        return mAudioWavPath;
    }

    /**
     * 判断是否有外部存储设备sdcard
     *
     * @return true | false
     */
    public static boolean isSdcardExit() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 获取全部pcm文件列表
     *
     * @return
     */
    public static List<File> getPcmFiles() {
        List<File> list = new ArrayList<>();
        String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_PCM_BASEPATH;

        File rootFile = new File(fileBasePath);
        if (!rootFile.exists()) {
        } else {

            File[] files = rootFile.listFiles();
            for (File file : files) {
                list.add(file);
            }

        }
        return list;

    }

    /**
     * 获取全部wav文件列表
     *
     * @return
     */
    public static List<File> getWavFiles() {
        List<File> list = new ArrayList<>();
        String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH;

        File rootFile = new File(fileBasePath);
        if (!rootFile.exists()) {
        } else {
            File[] files = rootFile.listFiles();
            for (File file : files) {
                list.add(file);
            }

        }
        return list;
    }
}
