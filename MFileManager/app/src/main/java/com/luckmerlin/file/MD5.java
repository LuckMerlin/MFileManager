package com.luckmerlin.file;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.util.Closer;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class MD5 {

    public String getFileMD5(File file) {
        return getFileMD5(file,null);
    }


    public String getFileMD5(File file, Cancel cancel) {
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
                    if (null!=cancel&&cancel.isCanceled()){
                        return null;
                    }
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
            if (null!=cancel&&cancel.isCanceled()){
                return null;
            }
            int v = bytes[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                buffer.append(0);
            }
            buffer.append(hv);
        }
        return buffer.toString();
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
