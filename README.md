# fsrmi
一个简易的rpc框架，由java实现（仅作课程作业用途）
## 项目简介
该项目是一个简单的课程作业，具体要求为模拟一个RPC调用过程。
## 开始使用
项目分为三个部分，按启动顺序分别是：注册中心，服务端以及客户端（***启动顺序不可更改***）<br>
***注册中心***不需要特别进行改动即可简单使用，如有特殊要求可以进行改动。注册中心的启动类为***RegisterContainer.class***类<br>
***服务端***将要调用的方法继承对应的接口并使用@RpcService注解进行标注即可再服务端启动时进行注册。服务端启动例子为***TestPrintfServer.class***。<br>
具体代码如下：
```
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
```
使用时可以自行增加相关逻辑，只需先实例化并启动ServerBoot()线程，然后其启动RpcServer（）线程监听请求，最后启动Consumer()线程进行消费即可。<br>
最后启动***客户端***即可实现简单的RPC过程。<br>
***客户端***模板为***TestPrintfClient.class类***<br>
```
public class TestPrintfClient {
    public static void main(String[] args) {
        //获得代理类
        Logger logger =  (Logger) RpcProxyClient.getProxy(new com.sict.fsrmi.client.Logger());
        //通过代理类进行调用
        String result = logger.printf("多次调用测试");
        System.out.println(result);
    }
}
```
通过RpcProxyClient类获得对应接口的代理类，再通过代理类进行RPC调用。<br>
***注***:客户端的实现不需要有具体的逻辑，但是需要和服务端提供的服务实现同一个接口。
# 项目结构
```
com
└─sict
    └─fsrmi
        ├─client        //客户端相关
        ├─common        //公共类
        │  └─entry      //公共实体
        ├─register      //注册中心
        │  └─entry      //实体类
        └─server        //服务端
            └─annotation    //注解相关
```