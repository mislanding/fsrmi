/**
 * @author lyy
 * @data 2020/10/3
 * @version 1.0
 */
package com.sict.fsrmi.server;

import com.sict.fsrmi.server.annotation.RpcService;

@RpcService(value ="Logger")
public class Logger implements com.sict.fsrmi.common.Logger {
    @Override
    public String printf(int x) {
        System.out.println("调用成功Printf方法:"+x);
        return "调用成功";
    }
}
