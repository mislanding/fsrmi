/**
 * @author lyy
 * @data 2020/10/4
 * @version 1.0
 */
package com.sict.fsrmi.client;

import com.sict.fsrmi.common.RpcRequest;
import com.sict.fsrmi.common.RpcResponse;
import com.sict.fsrmi.register.entry.Service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 代理方法类，生成代理对象，并调用远程方法
 *
 * @author lyy
 * @date 2020年10月4日
 */
public class RpcProxyClient implements InvocationHandler {

    private Object object;

    public RpcProxyClient(Object object) {
        this.object = object;
    }

    /**
     * 得到被代理对象
     */
    public static Object getProxy(Object obj) {
        return java.lang.reflect.Proxy.newProxyInstance(obj.getClass().getClassLoader(),
                obj.getClass().getInterfaces(), new RpcProxyClient(obj));
    }


    /**
     * 调用此方法执行
     * 远程通信逻辑
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //结果参数
        Object result = new Object();
        //创建一个socket线程，进行消息的发送
        RpcClient socket = new RpcClient("localhost", 8989);
        RegisterClient registerClient = new RegisterClient("localhost", 9090);
        //启动线程
        socket.start();
        registerClient.start();
        while (registerClient.serviceHashMap == null) {
            System.out.println("waiting");
        }
        String classname = method.getDeclaringClass().getCanonicalName();
        String[] name = classname.split("\\.");
        Service service = registerClient.serviceHashMap.get(name[name.length - 1]);
        RpcRequest request = new RpcRequest();
        request.setRequestId("122l42h1l");
        request.setClassName(service.getClassName());
        request.setMethodName(method.getName());
        request.setParamTypes(method.getParameterTypes());
        request.setParams(args);
        socket.write(request);
        while (socket.read(request.getRequestId())==null) {
            System.out.println("waiting");
        }
        RpcResponse response = socket.read(request.getRequestId());
        result = response.getResult();
        return result;
    }

}
