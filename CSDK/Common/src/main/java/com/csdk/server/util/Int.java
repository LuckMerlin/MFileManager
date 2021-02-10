package com.csdk.server.util;

/**
 * Created by Administrator on 2020/7/30.
 */

public class Int {
    public final static int BYTE_MASK = 0xFF; // 8 bits
    public final static int BSIZE = Byte.SIZE / Byte.SIZE;
    public final static int SSIZE = Short.SIZE / Byte.SIZE;
    public final static int ISIZE = Integer.SIZE / Byte.SIZE;
    public final static int LSIZE = Long.SIZE / Byte.SIZE;
    /**
	 * An empty instance.
	 */
	public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    public static int encodeIntBigEndian(byte[] dst, long val, int offset, int size) {
        for (int i = 0; i < size; i++) {
            dst[offset++] = (byte) (val >> ((size - i - 1) * Byte.SIZE));
        }
        return offset;
    }

    public static long decodeIntBigEndian(byte[] val, int offset, int size) {
        long rtn = 0;
        for (int i = 0; i < size; i++) {
            rtn = (rtn << Byte.SIZE) | ((long) val[offset + i] & BYTE_MASK);
        }
        return rtn;
    }

    public static byte[] add(final byte[] a, final byte[] b) {
        return add(a, b, EMPTY_BYTE_ARRAY);
    }

    public static byte[] add(final byte[] a, final byte[] b, final byte[] c) {
        byte[] result = new byte[a.length + b.length + c.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        System.arraycopy(c, 0, result, a.length + b.length, c.length);
        return result;
    }

    public static String bytesTo16String(byte[] b) {
        if (null!=b&&b.length>0){
            StringBuilder sb = new StringBuilder();
            for(int i=0;i<b.length;i++) {
                sb.append(" "+String.format("%02x", b[i]));
            }
            return sb.toString();
        }
        return null;
    }

    public static byte[] tail(final byte[] a, final int length) {
        if (a.length < length) {
            return null;
        }
        byte[] result = new byte[length];
        System.arraycopy(a, a.length - length, result, 0, length);
        return result;
    }

    /**
     * @param a
     *            array
     * @param length
     *            amount of bytes to snarf
     * @return Last <code>length</code> bytes from <code>a</code>
     */
    public static byte[] tail(final byte[] a,final int beginPos, final int length) {
        if (a.length < length) {
            return null;
        }
        byte[] result = new byte[length];
        System.arraycopy(a, beginPos, result, 0, length);
        return result;
    }

    public static Integer parseInteger(String intString,Integer def){
        if (null!=intString&&intString.length()>0){
            try {
                return Integer.parseInt(intString);
            }catch (Exception e){
                return def;
            }
        }
        return def;
    }


}
