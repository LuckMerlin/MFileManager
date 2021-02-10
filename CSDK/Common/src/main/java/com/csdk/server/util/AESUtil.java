package com.csdk.server.util;

//import org.apache.commons.codec.binary.Base64;
//import org.bouncycastle.jce.provider.BouncyCastleProvider;

import android.util.Base64;

import com.csdk.debug.Logger;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Security;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @deprecated
 */
public class AESUtil {


    private static final String ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";//默认的加密算法
    private static final String DEFAULT_ENCODING = "UTF-8";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static byte[] encryptToByte(String data, String key) {
        String ivStr = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        byte[] iv = ivStr.getBytes();
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
            Key sKeySpec = new SecretKeySpec(key.getBytes(DEFAULT_ENCODING), ALGORITHM);
            AlgorithmParameters params = AlgorithmParameters.getInstance(ALGORITHM);
            params.init(new IvParameterSpec(iv));
            cipher.init(Cipher.ENCRYPT_MODE, sKeySpec, params);
            byte[] encByte = cipher.doFinal(data.getBytes(DEFAULT_ENCODING));
            byte[] result = new byte[iv.length + encByte.length];
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(encByte, 0, result, iv.length, encByte.length);
            return result;
        } catch (Exception e) {
            Logger.M("Exception encrypt to byte.","Exception encrypt."+e);
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptFromByte(byte[] data, String key) {
        Cipher cipher;
        try {
            byte[] ivb = new byte[16];
            byte[] content = new byte[data.length - 16];
            for (int i = 0; i < data.length; i++) {
                if (i > 15) {
                    content[i - 16] = data[i];
                } else {
                    ivb[i] = data[i];
                }
            }
            cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
            Key sKeySpec = new SecretKeySpec(key.getBytes(DEFAULT_ENCODING), ALGORITHM);
            AlgorithmParameters params = AlgorithmParameters.getInstance(ALGORITHM);
            params.init(new IvParameterSpec(ivb));
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec, params);
            byte[] result = cipher.doFinal(content);
            return new String(result, DEFAULT_ENCODING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String encrypt(String input, String key) {
        try {
            return Base64.encodeToString(encryptToByte(input, key),Base64.DEFAULT|Base64.NO_WRAP);
        } catch (Exception e) {
            Logger.M("Exception encrypt.","Exception encrypt."+e);
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String input, String key) {
        try {
            return decryptFromByte(Base64.decode(input,Base64.DEFAULT), key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


//    public byte[] encrypt(String clear,String password)  {
//        Cipher cipher = null;
//        try {
//            SecretKeySpec secretKeySpec = new SecretKeySpec(password.getBytes(), "AES/CBC/PKCS5PADDING");
//            cipher = Cipher.getInstance("AES");
//            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
//            byte[] bytes=cipher.doFinal(clear.getBytes("UTF-8"));
//            Base64.encodeToString(bytes, Base64.DEFAULT);
//            return cipher.doFinal(clear.getBytes("UTF-8"));
//        } catch (Exception e) {
//            Debug.E("Exception encrypt aes "+e);
//            e.printStackTrace();
//        }
//        return null;
//    }

//    private byte[] decrypt(byte[] content, String password) throws Exception {
//        // 创建AES秘钥
//        SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES/CBC/PKCS5PADDING");
//        // 创建密码器
//        Cipher cipher = Cipher.getInstance("AES");
//        // 初始化解密器
//        cipher.init(Cipher.DECRYPT_MODE, key);
//        // 解密
//        return cipher.doFinal(content);
//    }



}