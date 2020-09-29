package com.brul.fsrmi.server.impl;

import com.brul.fsrmi.server.Service;

/**
 * @author lyy
 * @date 2020年9月29日
 * 通用服务类
 */
public class CommonService implements Service {
    private String servAdr;
    private String port;

    public String getPort() {
        return port;
    }

    public String getServAdr() {
        return servAdr;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setServAdr(String servAdr) {
        this.servAdr = servAdr;
    }
}
