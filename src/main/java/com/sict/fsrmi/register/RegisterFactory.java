/**
 * @author lyy
 * @data 2020/10/3
 * @version 1.0
 */
package com.sict.fsrmi.register;

import com.sict.fsrmi.common.RegRequest;
import com.sict.fsrmi.common.STATUS;
import com.sict.fsrmi.register.entry.Client;
import com.sict.fsrmi.register.entry.Service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegisterFactory implements Register {
    /**
     * 服务列表
     */
    private static HashMap<String, Service> serviceList;
    /**
     * 客户列表
     */
    private static List<Client> clientList;

    static {
        serviceList = new HashMap<>();
        clientList = new ArrayList<>();
    }

    /**
     * 注册服务列表
     */
    @Override
    public void registerService(Service service) {
        String serviceName = service.getServiceName();
        serviceList.put(serviceName, service);
    }

    /**
     * 注册用户列表
     */
    @Override
    public void registerClient(Client client) {
        clientList.add(client);
    }

    /**
     * 向客户返回服务列表
     *
     * @return HashMap<String, Service>
     */
    @Override
    public HashMap<String, Service> returnServiceList() {
        return serviceList;
    }

    /**
     * 下线服务
     */
    @Override
    public void deleteService(Service service) {
        try {
            //从服务列表中移除服务
            serviceList.remove(service);
            //通知客户
            notifyClient(service);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通知客户节点下线，并要求重新注册
     *
     * @param service
     * @throws IOException
     */
    public void notifyClient(Service service) throws IOException {
        //创建与客户机的链接
        Socket socket = new Socket();
        for (Client client : clientList) {
            socket.connect(new InetSocketAddress(client.getIp(),
                            client.getPort()),
                    5000);
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            RegRequest request = new RegRequest();
            request.setCode(STATUS.RE_MONITOR);
            request.setObject(null);
            //关闭输出流
            outputStream.close();
        }
        //关闭链接
        socket.close();

    }
}
