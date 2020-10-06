/**
 * @author lyy
 * @data 2020/10/3
 * @version 1.0
 */
package com.sict.fsrmi.common;

public interface Logger{
    /**
     * print函数
     * @param str
     * @return
     */
    public String printf(String str);

    /**
     * 格式化print函数
     * @param str
     * @param args
     * @return
     */
    public String printf(String str, Object... args);
}
