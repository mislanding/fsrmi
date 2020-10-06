package com.sict.fsrmi.common;

/**
 * @author lyy
 * @version 1.0
 * @data 2020/10/6
 * Byte数组常用方法类
 */
public class ByteUtil {
    /**
     * int 转 byte[]   低字节在前（低字节序）
     * @param n
     * @return
     */
    public static byte[] toLH(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }

    /**
     * byte[] 转 int 低字节在前（低字节序）
     * @param b
     * @return
     */
    public static int toInt(byte[] b) {
        int res = 0;
        for (int i = 0; i < b.length; i++) {
            res += (b[i] & 0xff) << (i * 8);
        }
        return res;
    }
}
