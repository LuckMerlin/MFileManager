package com.luckmerlin.file;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.util.Closer;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public final class MD5 {
    public interface OnProgressChange{
        boolean OnProgressChanged(long done,long total,float speed);
    }

    public String getFileMD5(File file) {
        return getFileMD5(file,null);
    }

    public String getFileMD5(File file, OnProgressChange callback) {
        if (null==file||!file.exists()) {
            return null;
        }
        final long total=file.isDirectory()?0:file.length();
        if (total<=0){
            return "";
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        try {
            digest = MessageDigest.getInstance("MD5");
            if (null!=digest){
                byte buffer[] = new byte[1024*1024];
                in = new FileInputStream(file);
                float speed=0;
                int read;long done=0;
                long startTime=System.nanoTime();
                while ((read = in.read(buffer)) >=0) {
                    if (read<=0){
                        continue;
                    }
                    digest.update(buffer, 0, read);
                    done+=read;
                    if ((startTime=startTime>0?System.nanoTime()-startTime:-1)>0){
                        startTime= TimeUnit.NANOSECONDS.toMillis(startTime);
                        speed=startTime>0?read/startTime:0;
                    }
                    if (null!=callback&&!callback.OnProgressChanged(done,total,speed)){
                        digest.reset();
                        digest=null;
                        break;
                    }
                    startTime=System.nanoTime();
                }
            }
        } catch (Exception e) {
            Debug.E("Exception get file MD5.e="+e,e);
            e.printStackTrace();
        }finally {
            new Closer().close(in);
        }
        byte[] bytes=null!=digest?digest.digest():null;
        if (bytes != null && bytes.length > 0) {
            StringBuffer buffer  = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                int v = bytes[i] & 0xFF;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    buffer.append(0);
                }
                buffer.append(hv);
            }
            return buffer.toString();
        }
        return null;
    }

    public String md5(String input) {
        if(input == null || input.length() == 0) {
            return null;
        }
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(input.getBytes());
            byte[] byteArray = md5.digest();

            StringBuilder sb = new StringBuilder();
            for (byte b : byteArray) {
                // 一个byte格式化成两位的16进制，不足两位高位补零
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
