/**
 * @author lyy
 * @data 2020/10/4
 * @version 1.0
 */
package com.sict.fsrmi.client;

import com.sict.fsrmi.common.RpcRequest;
import com.sict.fsrmi.common.RpcResponse;
import com.sict.fsrmi.register.entry.Service;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;

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
     * 根据IP和端口号，查询其是否被占用
     * @param host  IP
     * @param port  端口号
     * @return  如果被占用，返回true；否则返回false
     * @throws UnknownHostException    IP地址不通或错误，则会抛出此异常
     */
    public static boolean isPortUsing(String host, int port) throws UnknownHostException {
        boolean flag = false;
        InetAddress theAddress = InetAddress.getByName(host);
        try {
            Socket socket = new Socket(theAddress, port);
            flag = true;
        } catch (IOException e) {
            //如果所测试端口号没有被占用，那么会抛出异常，这里利用这个机制来判断
            //所以，这里在捕获异常后，什么也不用做
        }
        return flag;
    }
        /**
         * 得到被代理对象
         */
    public static Object getProxy(Object obj) {
        return java.lang.reflect.Proxy.newProxyInstance(obj.getClass().getClassLoader(),
                obj.getClass().getInterfaces(), new RpcProxyClient(obj));
    }

    /**
     * 通过UUID生成全局唯一标识requestID
     * @return
     */
    public String getUUID(){
        return UUID.randomUUID().toString().substring(24);
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
        RpcRequest request = new RpcRequest();
        //启动线程
        socket.start();
        //具体过程
        //1、首先获得registerClient线程的同步锁
        //2、在同步区域内启动registerCliet线程
        //3、当服务列表为空时，释放register的同步锁并进入等待状态
        //4、register获得服务列表后，唤醒主线程，程序继续执行
        synchronized (registerClient) {
            registerClient.start();
            while (registerClient.serviceHashMap == null) {
                //等待
                registerClient.wait();
            }
        }
        String classname = method.getDeclaringClass().getCanonicalName();
        String[] name = classname.split("\\.");
        Service service = registerClient.serviceHashMap.containsKey(name[name.length-1])?registerClient.serviceHashMap.get(name[name.length - 1]):null;
        if(service==null){
            System.out.println("没有对应的服务，请确认是否出错");
            return "没有对应的服务，请确认是否出错";
        }
        //获取全局唯一ID
        request.setRequestId(getUUID());
        //通过规定的命名规则获取服务名
        request.setClassName(service.getClassName());
        //获得方法名
        request.setMethodName(method.getName());
        //设置方法的参数类型
        request.setParamTypes(method.getParameterTypes());
        //设置方法的参数
        request.setParams(args);
        //发送请求（非阻塞）
        socket.write(request);
        //1、同步块获取socket的同步锁
        //2、当还未获得结果时，主线程进入等待状态，释放socket的同步锁
        //3、socket获得新消息时唤醒主线程，主线程尝试获得响应消息，若无则返回第二步
        //重发次数
        int i=0;
        synchronized (socket){
            while (socket.read(request.getRequestId()) == null) {
                //设置200ms超时，若无反应则再次发送
                socket.wait(4000);
                if(socket.read(request.getRequestId()) == null){
                    if (i >= 3) {
                        //重发三次后仍然无结果则不再发送
                        throw new RuntimeException("方法调用失败，请检查服务器是否出错");
                    }
                    new RpcClient("localhost", 8989).write(request);
                    i++;

                }

            }
        }
        RpcResponse response = socket.read(request.getRequestId());
        result = response.getResult();
        return result;
    }

}
