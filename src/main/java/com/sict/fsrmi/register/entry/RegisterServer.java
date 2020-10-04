/**
 * @author lyy
 * @data 2020/10/4
 * @version 1.0
 */
package com.sict.fsrmi.register.entry;

import com.alibaba.fastjson.JSONObject;
import com.sict.fsrmi.common.JsonSerializer;
import com.sict.fsrmi.common.RegRequest;
import com.sict.fsrmi.common.STATUS;
import com.sict.fsrmi.register.RegisterFactory;
import lombok.SneakyThrows;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class RegisterServer extends Thread {

    public RegisterServer(){};
    private static ServerSocket serverSocket;
    private static RegisterFactory registerFactory;

    /**
     * 利用java的类加载机制，在实例化类时会自动生成对应的对象属性
     */
    static{
        try {
            serverSocket = new ServerSocket(9090);
            registerFactory = new RegisterFactory();
        } catch (IOException e) {
            System.out.println("注册中心创建失败");
            e.printStackTrace();
        }
    }

    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("客户端:"+socket.getInetAddress().getHostAddress()+"已连接到服务器");
            DataInputStream input = new DataInputStream(socket.getInputStream());
            byte[] buffer = new byte[4096];
            int length = input.read(buffer);
            System.out.println(length);
            //读取客户端发来的信息
            RegRequest request = new JsonSerializer().deserialize(RegRequest.class, buffer);
            //当服务端节点下线时
            if (request.getCode().equals(STATUS.DELETE)) {
                //在注册中心删除服务
                registerFactory.deleteService((Service) request.getObject());
            }
            //客户端请求注册监听
            if (request.getCode().equals(STATUS.MONITOR)) {
                //传递过来的实体为JSONObject无法直接通过java进行转换，故需要先转化为JSONObject再转化为对应类型
                JSONObject object =(JSONObject) request.getObject();
                Client client = JSONObject.toJavaObject(object, Client.class);
                //注册客户
                registerFactory.registerClient(client);
                HashMap map = registerFactory.returnServiceList();
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                RegRequest response = new RegRequest();
                response.setCode(STATUS.RETURNLIST);
                response.setObject(map);
                //返回服务列表
                outputStream.write(new JsonSerializer().serialize(response));
            }
            //服务端请求注册服务
            if (request.getCode().equals(STATUS.REGISTER)) {
                JSONObject object = (JSONObject) request.getObject();
                Service service = JSONObject.toJavaObject(object, Service.class);
                service.setIp(socket.getInetAddress().getHostAddress());
                registerFactory.registerService(service);
                System.out.println("服务"+service+"注册成功");
            }
            socket.close();
        }
    }
}
