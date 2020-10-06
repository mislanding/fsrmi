package com.sict.fsrmi.client;

import com.sict.fsrmi.common.*;
import com.sict.fsrmi.common.entry.RpcResponse;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * @author lyy
 * @date 2020年9月29日
 * 客户端，主要作用链接管理，读写消息，链接重连
 */
public class RpcClient extends Thread {
    private String ip;
    private int port;
    private volatile static SocketChannel socket;
    private static Selector selector;

    static {
        try {
            selector = RpcSelector.getSelector();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public volatile static Hashtable<String, RpcResponse> responseList = new Hashtable();

    public RpcClient(String ip, int port) {
        super();
        this.ip = ip;
        this.port = port;
    }

    /**
     * 写方法
     * @param request
     */
    public void write(Object request) {
        try {
            socket.register(selector,
                    SelectionKey.OP_WRITE,
                    ByteBuffer.wrap(new JsonSerializer().serialize(request)));
            //唤醒由于OP_READ而阻塞的selector
            selector.wakeup();
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读方法，获取请求对应的响应
     * @param requestId
     * @return
     */
    public RpcResponse read(String requestId) {
        synchronized (this){
            if (responseList.containsKey(requestId)) {
                notifyAll();
            }
        }
        return responseList.get(requestId);
    }

    @Override
    public void run() {
        try {
            //初始化链接客户端
            //使用双重校验，保证线程安全
            if(socket==null){
                synchronized (SocketChannel.class) {
                    if (socket == null) {
                        socket = SocketChannel.open();
                    }
                }
            }
            socket.configureBlocking(false);
            //注册链接事件
            socket.register(selector, SelectionKey.OP_CONNECT);
            //发起连接
            socket.connect(new InetSocketAddress(ip, port));
            Calendar ca = Calendar.getInstance();
            while (true) {
                if (socket.isOpen()) {
                    //在注册的键中选择已就绪的事件
                    selector.select();
                    //已选择键集
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = keys.iterator();
                    //处理准备就绪的事件
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        //删除当前键，避免重复消费
                        iterator.remove();
                        if (key.isConnectable()) {
                            while (!socket.finishConnect()) {
                                System.out.println("连接中");
                            }
                            socket.register(selector, SelectionKey.OP_READ);
                        }
                        //获取到输入的信息，注册OP_WRITE,然后将消息附在attachment中
                        if (key.isWritable()) {
                            socket.write((ByteBuffer) key.attachment());
                            /**
                             * 由于nio的write事件并不会产生阻塞，故事件已经处理完毕，因此要改为监听OP_READ事件
                             */
                            socket.register(selector, SelectionKey.OP_READ);
                            System.out.println("==============" + ca.getTime() + " ==============");
                            System.out.println("==============发送请求============================");
                        }
                        if (key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(409600);
                            int len = 0;
                            try {
                                if ((len = socket.read(buffer)) > 0) {
                                    //将收到的消息反序列化，存入收到回复的表中,通过对应的RequestId获取
                                    //请求对应的相应
                                    RpcResponse rpcResponse = new RpcResponse().deserialize(RpcResponse.class,
                                            buffer.array());
                                    System.out.println("收到来自服务器的消息，RequestId="+ rpcResponse.getRequestId());
                                    responseList.put(rpcResponse.getRequestId(), rpcResponse);
                                    synchronized (this){
                                        notifyAll();
                                    }
                                }
                            } catch (IOException e) {
                                System.out.println("服务器异常，请联系客服人员!");
                                key.cancel();
                                socket.close();
                            }
                            System.out.println("=========================================================");
                        }
                    }
                }else {
                    continue;
                }

            }
        } catch (IOException e) {
            System.out.println("客户端异常，请重新启动"
            );
        }catch (AlreadyConnectedException e){
            System.out.println("服务器端口正在被占用，请稍后");
        }
    }
}
