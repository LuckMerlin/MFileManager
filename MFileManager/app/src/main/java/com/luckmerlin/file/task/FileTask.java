package com.luckmerlin.file.task;

import com.luckmerlin.file.Path;
import com.luckmerlin.task.FromToTask;
import com.luckmerlin.task.ProgressTask;
import com.luckmerlin.task.Task;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

public abstract class FileTask extends FromToTask<Path,Path> implements ProgressTask {
    protected final static String POST="POST";
    protected final static String GET="GET";
    protected final static String HEAD="HEAD";
    private boolean mEnableBreakpoint=false;

    public FileTask(Path from,Path to){
        super(null!=to?to.getName():null!=from?from.getName():null,from,to);
    }

    public final long getTotal() {
        Path path= getFrom();
        return null!=path?path.getTotal():0;
    }

    public final long getSize() {
        Path path= getTo();
        return null!=path?path.getTotal():0;
    }

    public final boolean isEnableBreakpoint() {
        return mEnableBreakpoint;
    }

    protected final HttpURLConnection createHttpConnect(String urlPath, String method) throws IOException {
        return createHttpConnect(urlPath,method,null);
    }

    protected final HttpURLConnection createHttpConnect(String urlPath, String method, Map<String,String> properies) throws IOException {
        URL url=null!=urlPath&&urlPath.length()>0?new URL(urlPath):null;
        HttpURLConnection conn = null!=url?(HttpURLConnection) url.openConnection():null;
        if (null!=conn){
            conn.setRequestMethod(null!=method&&method.length()>0?method:GET);
            conn.setRequestProperty("Charset", "UTF-8");
            Set<String> set=null!=properies?properies.keySet():null;
            if (null!=set){
                for (String child:set) {
                    String value=null!=child?properies.get(child):null;
                    if (null!=value){
                        conn.setRequestProperty(child,value);
                    }
                }
            }
            conn.setUseCaches(false);
            return conn;
        }
        return null;
    }

    @Override
    public final float getProgress() {
        long total=getTotal();
        long size=getSize();
        return total>0&&size>=0?size/(float)total:0;
    }
}
