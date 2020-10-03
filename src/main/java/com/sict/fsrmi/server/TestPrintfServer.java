/**
 * @author lyy
 * @data 2020/10/3
 * @version 1.0
 */
package com.sict.fsrmi.server;

public class TestPrintfServer {
    public static void main(String[] args) {
        RpcServer server = RpcServer.getInstance(8989);
        //启动socket线程监听消息并加入消费队列
        server.start();
        Consumer consumer = new Consumer(server);
        consumer.start();
    }
}
