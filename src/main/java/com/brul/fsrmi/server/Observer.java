package com.brul.fsrmi.server;

import java.util.Vector;

/**
 * @author lyy
 * @date 2020年9月29日
 * @see com.brul.fsrmi.server.Observer
 * 观察者，用于保存注册服务的用户
 */
public interface Observer {
    /**
     * 更新用户状态
     */
    public void update();
}
