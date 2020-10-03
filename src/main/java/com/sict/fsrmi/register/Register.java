/**
 * @author lyy
 * @data 2020/10/3
 * @version 1.0
 */
package com.sict.fsrmi.register;

import java.util.List;

public interface Register {
    /**
     * 注册服务列表
     */
    public void registerService();

    /**
     * 注册用户列表
     */
    public void registerClient();

    /**
     * 向客户返回服务列表
     * @return List<Service>
     *
     */
    public List<Service> returnServiceList();

    /**
     * 通知客户节点下线
     */
    public void notifyClient();
}
