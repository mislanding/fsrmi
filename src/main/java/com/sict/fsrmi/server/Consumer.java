/**
 * @author lyy
 * @data 2020/10/3
 * @version 1.0
 */
package com.sict.fsrmi.server;

import com.sict.fsrmi.common.RpcDecoder;
import com.sict.fsrmi.common.RpcRequest;
import com.sict.fsrmi.common.RpcResponse;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class Consumer extends Thread {
    private static volatile Hashtable<String, RpcRequest> consumerList;
    private RpcServer server;

    /**
     * 构造方法，绑定唯一Socket
     * @param server
     */
    public Consumer(RpcServer server) {
        this.server = server;
    }
    @Override
    public void run() {
        while(true){
            //获取消费
            if(consumerList==null){
                consumerList = server.getList();
            }
            //获取迭代器
            Iterator<Map.Entry<String, RpcRequest>> iterator = consumerList.entrySet().iterator();
            while (iterator.hasNext()) {
                try {
                    Map.Entry map = iterator.next();
                    RpcRequest request = (RpcRequest) map.getValue();
                    //获取对象
                    Object object = RpcDecoder.getRpcDecoder().getObject(request);
                    //获取方法
                    Method method = RpcDecoder.getRpcDecoder().getMethod(request);
                    //通过反射进行调用
                    Object result = method.invoke(object, request.getParams());
                    RpcResponse response = new RpcResponse();
                    response.setResult(result);
                    response.setRequestId(request.getRequestId());
                    response.setError("调用成功");
                    server.write(response);
                    //消费完毕后由消费队列中删除
                    consumerList.remove(request.getRequestId());
                    System.out.println("========================================");
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }
}
