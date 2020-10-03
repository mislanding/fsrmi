/**
 * @author lyy
 * @data 2020/10/3
 * @version 1.0
 */
package com.sict.fsrmi.client;

import com.sict.fsrmi.Log;
import com.sict.fsrmi.common.RpcRequest;

public class TestPrintfClient {
    public static void main(String[] args) {
        //创建一个socket线程，进行消息的发送
        RpcClient socket = new RpcClient("localhost", 8989);
        //启动线程
        socket.start();
        RpcRequest request = new RpcRequest();
        request.setRequestId("122l42h1l");
        request.setClassName("com.sict.fsrmi.server.Log");
        request.setMethodName("printf");
        socket.write(request);

    }
}
