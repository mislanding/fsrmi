/**
 * @author lyy
 * @data 2020/10/4
 * @version 1.0
 */
package com.sict.fsrmi.client;


import com.alibaba.fastjson.JSONObject;
import com.sict.fsrmi.common.JsonSerializer;
import com.sict.fsrmi.common.RegRequest;
import com.sict.fsrmi.common.STATUS;
import com.sict.fsrmi.register.entry.Client;
import com.sict.fsrmi.register.entry.Service;

import lombok.SneakyThrows;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RegisterClient extends Thread {
    private static Socket socket;
    private static ServerSocket serverSocket;
    private String ip;
    private int port;
    public HashMap<String, Service> serviceHashMap;
    static {
        try {
            serverSocket = new ServerSocket(9081);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public
    RegisterClient(String ip, int port) throws IOException {
        super();
        this.ip = ip;
        this.port = port;
        socket = new Socket();
        socket.connect(new InetSocketAddress(ip, port));

    }

    /**
     * 注册监听，并获取最新的服务列表
     * @return
     * @throws IOException
     */
    public void Monitor() throws IOException {
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        //创建客户实体
        Client client = new Client();
        client.setPort(9091);
        RegRequest request = new RegRequest();
        //设置状态码
        request.setCode(STATUS.MONITOR);
        request.setObject(client);
        output.write(new JsonSerializer().serialize(request));
        //获取返回的服务列表
        DataInputStream in = new DataInputStream(socket.getInputStream());
        byte[] b = new byte[4096];
        int length = in.read(b);
        //反序列化
        RegRequest response = new JsonSerializer().deserialize(RegRequest.class, b);
        //传递过来的实体为JSONObject无法直接通过java进行转换，故需要先转化为JSONObject再转化为对应类型
        JSONObject object = (JSONObject) response.getObject();
        Iterator iterator = object.entrySet().iterator();
        HashMap<String, Service> map = new HashMap<>();
        while (iterator.hasNext()) {
            Map.Entry<String, JSONObject> serviceEntry = (Map.Entry<String, JSONObject>)iterator.next();
            map.put(serviceEntry.getKey(),
                    JSONObject.toJavaObject((JSONObject) serviceEntry.getValue(),
                            Service.class));
        }
        serviceHashMap = map;
        System.out.println("=========================服务列表更新==================================");
        System.out.println(serviceHashMap);
    }

    @SneakyThrows
    @Override
    public void run() {
        Monitor();
        while(true){
            Socket socket = serverSocket.accept();
            DataInputStream in = new DataInputStream(socket.getInputStream());
            byte[] buffer = new byte[4096];
            int length = in.read(buffer);
            System.out.println(length);
            //读取客户端发来的信息
            RegRequest request = new JsonSerializer().deserialize(RegRequest.class, buffer);
            if (request.getCode().equals(STATUS.RE_MONITOR)) {
                Monitor();
            }
        }
    }
}
