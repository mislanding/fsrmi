package com.brul.fsrmi.server.impl;

import com.brul.fsrmi.server.Observer;

/**
 * @author lyy
 * @date 2020年9月29日
 *
 */
public class ObserverImpl implements Observer {
    private String clientAdress;
    private String port;
    private String hostname;
    private String status;
    /**
     * 基础构造方法
     */
    public ObserverImpl() {
    }
    /**
     * 更新注册的客户机的状态
     * @param
     */
    public void update() {
    }

    public void update(String clientAdress){
        this.clientAdress=clientAdress;
    }


    public void setClientAdress(String clientAdress){
        this.clientAdress = clientAdress;
    }
    public String getClientAdress(){
        return this.clientAdress;
    }
    public void setPort(String port){
        this.port = port;
    }
    public String getPort(){
        return this.port;
    }
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    public String getHostname(){
        return this.hostname;
    }
}
