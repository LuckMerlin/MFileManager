package com.luckmerlin.file;

import android.webkit.MimeTypeMap;

public final class Thumbs {

    public String getExtension(String path){
        int index=null!=path&&path.length()>=0?path.lastIndexOf("."):-1;
        return index>=0?path.substring(index):null;
    }

    public boolean isImageExtension(String extension){
        return null!=extension&&extension.length()>0&&(extension.equalsIgnoreCase(".jpg")||extension.equalsIgnoreCase(".jpeg")||
                extension.equalsIgnoreCase(".gif"));
    }

    public boolean isAudioExtension(String extension){
        return null!=extension&&extension.length()>0&&(extension.equalsIgnoreCase(".ogg")||extension.equalsIgnoreCase(".amr")||
                extension.equalsIgnoreCase(".mp3"));
    }

    public boolean isVideoExtension(String extension){
        return null!=extension&&extension.length()>0&&(extension.equalsIgnoreCase(".mp4")||extension.equalsIgnoreCase(".mkv")||
                extension.equalsIgnoreCase(".flv"));
    }

    public String getMimeType(String path){
        String extension=null!=path&&path.length()>0?getExtension(path):null;
        return null!=extension&&extension.length()>0?MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                extension.toLowerCase()):null;
    }

    public String getThumb(String path){
        String extension=null!=path&&path.length()>0?getExtension(path):null;
        return null!=extension&&extension.length()>0?(isVideoExtension(extension)||
                extension.equalsIgnoreCase(".mp3")||isImageExtension(extension))?path:null:null;
    }

}
