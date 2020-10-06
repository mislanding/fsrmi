package com.sict.fsrmi.common.entry;
/**
 * @author lyy
 * @data 2020/10/4
 * @version 1.0
 */
public class STATUS {
    /**
     *节点下线
     */
    public static final String DELETE = "001";
    /**
     * 注册监听
     */
    public static final String MONITOR = "002";
    /**
     * 注册服务
     */
    public static final String REGISTER = "003";
    /**
     *请求重新注册
     */
    public static final String RE_MONITOR = "004";
    /**
     * 返回服务列表
     */
    public static final String RETURNLIST = "005";
}
