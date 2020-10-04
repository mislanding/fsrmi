/**
 * @author lyy
 * @data 2020/10/3
 * @version 1.0
 */
package com.sict.fsrmi.server;

public class TestPrintfServer {
    public static void main(String[] args) {
        //扫描包下文件并注册服务
        new ServerBoot().start();
        RpcServer server = RpcServer.getInstance(8989);
        //启动socket线程监听消息并加入消费队列
        server.start();
        //启动消费者线程
        Consumer consumer = new Consumer(server);
        consumer.start();
    }
}
