package com.luckmerlin.file;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.util.Closer;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public final class MD5 {

    public String getFileMD5(File file) {
        if (null==file||!file.exists()||file.length()<=0||!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            if (null!=digest){
                byte buffer[] = new byte[1024*1024];
                in = new FileInputStream(file);
                while ((len = in.read(buffer)) != -1) {
                    digest.update(buffer, 0, len);
                }
            }
        } catch (Exception e) {
            Debug.E("Exception get file MD5.e="+e,e);
            e.printStackTrace();
        }finally {
            new Closer().close(in);
        }
        byte[] bytes=null!=digest?digest.digest():null;
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
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
}
