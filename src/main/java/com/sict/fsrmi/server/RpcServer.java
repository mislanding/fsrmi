package com.sict.fsrmi.server;

import com.sict.fsrmi.common.RpcRequest;
import com.sict.fsrmi.common.RpcResponse;
import com.sict.fsrmi.common.RpcSelector;
import com.sict.fsrmi.register.Client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * @author lyy
 * @date 2020年10月1日
 * 服务端，主要功能有链接管理，读写消息，消息消费（仅提供消费队列，具体消费由其他方法实现）
 */
public class RpcServer extends Thread {
    private String ip;
    private int port;
    private volatile static ServerSocketChannel socket;
    private static Selector selector;
    private volatile static RpcServer server;
    private volatile static Hashtable<String, RpcRequest> requestList = new Hashtable<>();
    private volatile static Hashtable<String, SocketChannel> clentList = new Hashtable<>();

    static {
        try {
            selector = RpcSelector.getSelector();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得实例方法
     *
     * @param port
     * @return
     */
    public static RpcServer getInstance(int port) {
        if (server == null) {
            synchronized (RpcServer.class) {
                if (server == null) {
                    server = new RpcServer(port);
                }
            }
        }
        return server;
    }

    private RpcServer(int port) {
        super();
        this.port = port;
    }

    /**
     * 写方法，向客户返回消费结果
     *
     * @param response
     */
    public void write(RpcResponse response) {
        try {
            //通过唯一的RequestId获取客户Socket
            SocketChannel client = clentList.get(response.getRequestId());
            //返回消费结果
            client.write(ByteBuffer.wrap(response.serialize(response)));
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }catch (IOException e){
            System.out.println("返回消费结果出错");
        }
    }

    /**
     * @return
     * @返回待消费的消息列表
     */
    public Hashtable getList() {
        return requestList;
    }

    @Override
    public void run() {
        try {
            //初始化客户端，使用双重校验保证线程安全
            if (socket == null) {
                synchronized (ServerSocketChannel.class) {
                    if (socket == null) {
                        socket = ServerSocketChannel.open();
                    }
                }
            }
            //设置非阻塞
            socket.configureBlocking(false);
            //绑定端口
            socket.bind(new InetSocketAddress("localhost", port));
            //注册监听事件
            socket.register(selector, SelectionKey.OP_ACCEPT);
            Calendar ca = Calendar.getInstance();
            System.out.println("服务端启动");
            System.out.println("===============================================");
            while (true) {
                //选择准备好的事件
                selector.select();
                //已选择的键集
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    //移除已处理的键，避免重复消费
                    iterator.remove();
                    //处理请求链接请求
                    if (key.isAcceptable()) {
                        //处理请求
                        SocketChannel socketChannel = socket.accept();
                        socketChannel.configureBlocking(false);
                        //注册监听，接收客户端发送来的信息
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        //输出客户端地址
                        InetSocketAddress address = (InetSocketAddress) socketChannel.getRemoteAddress();
                        System.out.println(ca.getTime() + "\t" + address.getHostString() +
                                ":" + address.getPort() + "\t");
                        System.out.println("客戶端已连接");
                        System.out.println("=========================================================");

                    }
                    //处理收到消息
                    if (key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        InetSocketAddress address = (InetSocketAddress) socketChannel.getRemoteAddress();
                        System.out.println(ca.getTime() + "\t" + address.getHostString() +
                                ":" + address.getPort() + "\t");
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int len = 0;
                        byte[] res = new byte[1024];
                        //捕获异常，因为在客户端关闭后会发送FIN报文，会触发read事件，但连接已关闭,此时read()会产生异常
                        try {
                            while ((len = socketChannel.read(buffer)) > 0) {
                                buffer.flip();
                                buffer.get(res, 0, len);
                                RpcRequest request = new RpcRequest().deserialize(RpcRequest.class, res);
                                requestList.put(request.getRequestId(), request);
                                clentList.put(request.getRequestId(), socketChannel);
                                buffer.clear();
                            }
                            System.out.println("=======================================================");

                        } catch (IOException e) {
                            //客户端关闭了
                            key.cancel();
                            socketChannel.close();
                            //这里要改
                            System.out.println("客户端已断开");
                            System.out.println("========================================================");
                        }
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("服务器异常，即将关闭");
            System.out.println("===============================================");
        }
    }
}
