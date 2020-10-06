/**
 * @author lyy
 * @data 2020/10/3
 * @version 1.0
 */
package com.sict.fsrmi.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sict.fsrmi.common.RpcRequest;
import com.sict.fsrmi.common.RpcResponse;

import javax.swing.text.html.HTMLDocument;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class Consumer extends Thread {
    private static volatile Hashtable<String, RpcRequest> consumerList;
    private static volatile Hashtable<String, RpcRequest> finRequestList;
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
            if(finRequestList==null){
                finRequestList = server.getFinList();
            }
            //获取迭代器
            Iterator<Map.Entry<String, RpcRequest>> iterator = consumerList.entrySet().iterator();
            while (iterator.hasNext()) {
                try {
                    Map.Entry map = iterator.next();
                    final RpcRequest request = (RpcRequest) map.getValue();
                    //获取对象
                    Object object = RpcDecoder.getRpcDecoder().getObject(request);
                    //获取方法
                    Method method = RpcDecoder.getRpcDecoder().getMethod(request);
                    Object[] params = request.getParams();
                    if (params.length>1&&params[1].getClass().equals(JSONArray.class)) {
                        JSONArray jsonArray = (JSONArray) params[1];
                        Object[] objects = new Object[jsonArray.size()];
                        for (int i = 0; i < jsonArray.size(); i++) {
                            objects[i] = jsonArray.get(i);
                        }
                        params[1] = objects;
                    }
                    //通过反射进行调用
                    Object result = new Object();
                    if(params.length>1){
                        result = method.invoke(object,params[0],(Object)params[1]);
                    }else {
                        result = method.invoke(object, params);
                    }
                    RpcResponse response = new RpcResponse();
                    response.setResult(result);
                    response.setRequestId(request.getRequestId());
                    response.setError("调用成功");
                    server.write(response);
                    synchronized (finRequestList.getClass()) {
                        //消费完成后加入已完成列表
                        finRequestList.put(request.getRequestId(), request);
                        //消费完毕后由消费队列中删除
                        consumerList.remove(request.getRequestId());
                    }
                    System.out.println("========================================");
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }
}
