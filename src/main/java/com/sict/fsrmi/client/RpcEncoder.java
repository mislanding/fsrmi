package com.sict.fsrmi.client;

import com.sict.fsrmi.common.RpcRequest;
import com.sict.fsrmi.common.RpcSelector;
import jdk.nashorn.internal.ir.RuntimeNode;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lyy
 * @date 2020年9月30日
 * 将方法对象转化为通用通信协议
 */
public class RpcEncoder{
    public static ConcurrentHashMap<String,String> serviceList = new ConcurrentHashMap<String, String>();
    private static SocketChannel socketChannel;
    static{
        try {
            if(socketChannel==null){
                socketChannel = SocketChannel.open();
            }
            socketChannel.register(RpcSelector.getSelector(), SelectionKey.OP_CONNECT);
            //链接到注册中心
            socketChannel.connect(new InetSocketAddress("localhost", 9090));
            while(true){

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static volatile  RpcEncoder rpcEncoder;
    private RpcEncoder() throws IOException {
    }
    public static RpcEncoder getRpcEncoder() throws IOException {
        if(rpcEncoder==null){
            synchronized (RpcEncoder.class){
                if (rpcEncoder == null) {
                    rpcEncoder = new RpcEncoder();
                }
            }
        }
        return rpcEncoder;
    }

    public RpcRequest invoke(String object,String methodName){
        RpcRequest request = new RpcRequest();
        request.setClassName(object);
        request.setMethodName(methodName);

        return request;
    }
}
