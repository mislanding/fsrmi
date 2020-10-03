package com.sict.fsrmi.common;

import lombok.Data;

/**
 * @author lyy
 * @date 2020年9月30日
 * 规定请求通信协议
 */
@Data
public class RpcRequest extends JsonSerializer{
    /**
     * 请求ID，保证仅执行一次任务
     */
    private String requestId;
    /**
     * 返回值类型
     */
    private Class returnType;
    /**
     * 类名
     */
    private String className;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 参数类型
     */
    private Class<?>[] paramTypes;
    /**
     * 参数值
     */
    private Object[] params;
}
