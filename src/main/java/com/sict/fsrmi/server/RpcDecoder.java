package com.sict.fsrmi.server;

import com.sict.fsrmi.common.entry.RpcRequest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author lyy
 * @date 2020年10月1日
 * 解码器
 */
public class RpcDecoder {
    private static volatile  RpcDecoder rpcDecoder;
    private RpcDecoder() {
    }

    public static RpcDecoder getRpcDecoder() {
        if (rpcDecoder == null) {
            synchronized (RpcDecoder.class) {
                if (rpcDecoder == null) {
                    rpcDecoder = new RpcDecoder();
                }
            }
        }
        return rpcDecoder;
    }

    /**
     * 获取实例化对象
     *
     * @param request
     * @return
     * @throws Exception
     */
    public Object getObject(RpcRequest request) throws Exception {
        //通过java反射机制动态生成java对象
        String classname = request.getClassName();
        Class clazz = Class.forName(classname);
        Constructor constructor = clazz.getConstructor();
        return constructor.newInstance();
    }

    /**
     * 获取实例化方法
     * @param request
     * @return
     * @throws Exception
     */
    public Method getMethod(RpcRequest request) throws Exception {
        //获取调用的方法
        String classname = request.getClassName();
        Class clazz = Class.forName(classname);
        return clazz.getMethod(request.getMethodName(),
                request.getParamTypes());
    }
}
