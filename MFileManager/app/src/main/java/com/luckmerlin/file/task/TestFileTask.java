package com.luckmerlin.file.task;

import com.luckmerlin.file.Path;
import com.luckmerlin.task.FromToTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

public abstract class TestFileTask<T extends Path,V extends Path> extends FromToTask<T,V> {
    protected final static String POST="POST";
    protected final static String GET="GET";
    protected final static String HEAD="HEAD";
    private boolean mEnableBreakpoint=false;

    public TestFileTask(T from, V to){
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

    public abstract long getPerSpeed();

    public final boolean isEnableBreakpoint() {
        return mEnableBreakpoint;
    }

    protected final long string2Long(String lengthValue,long def){
        try {
            return Long.parseLong(lengthValue);
        }catch (Exception e){
            return def;
        }
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
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setConnectTimeout(50000);
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
}
