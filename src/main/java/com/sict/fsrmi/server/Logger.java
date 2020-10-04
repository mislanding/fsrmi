/**
 * @author lyy
 * @data 2020/10/3
 * @version 1.0
 */
package com.sict.fsrmi.server;

import com.sict.fsrmi.server.annotation.RpcService;

@RpcService("Log")
public class Log implements com.sict.fsrmi.Log {
    @Override
    public void printf() {
        System.out.println("调用成功Printf方法");
    }
}
