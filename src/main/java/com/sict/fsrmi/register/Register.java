/**
 * @author lyy
 * @data 2020/10/3
 * @version 1.0
 */
package com.sict.fsrmi.register;

import com.sict.fsrmi.register.entry.Client;
import com.sict.fsrmi.register.entry.Service;

import java.util.HashMap;
import java.util.List;

public interface Register {
    /**
     * 注册服务列表
     * @param service
     */
    public void registerService(Service service);

    /**
     * 注册客户列表
     * @param client
     */
    public void registerClient(Client client);

    /**
     * 向客户返回服务列表
     * @return HashMap<String,Service>
     *
     */
    public HashMap<String,Service> returnServiceList();

    /**
     * 节点下线
     */
    public void deleteService(Service service);
}
