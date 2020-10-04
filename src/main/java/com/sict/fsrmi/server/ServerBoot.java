package com.sict.fsrmi.server;

import com.alibaba.fastjson.JSON;
import com.sict.fsrmi.common.JsonSerializer;
import com.sict.fsrmi.common.RegRequest;
import com.sict.fsrmi.common.STATUS;
import com.sict.fsrmi.register.entry.Service;
import com.sict.fsrmi.server.annotation.AnnotationScannerUtils;
import com.sict.fsrmi.server.annotation.RpcService;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

/**
 * @author lyy
 * @version 1.0
 * @data 2020/10/4
 * 启动服务，通过扫描注解将服务注册至注册中心
 */

public class ServerBoot {
    public ServerBoot() {

    }
    public void start() {
        String pkgName = "com"+"."+"sict"+"."+"fsrmi"+"."+"server";
        String pkgPath = AnnotationScannerUtils.getPkgPath(pkgName);
        Map<Class<? extends Annotation>, Set<Class<?>>> classMap = AnnotationScannerUtils.scanClassesByAnnotations(pkgName,
                pkgPath,
                true,
                RpcService.class);
        if (classMap.size() == 0) {
            System.out.println("没有要注册的服务");
            return;
        }

        Set<Class<?>> classSet = new HashSet<>();
        classSet.addAll(classMap.get(RpcService.class));
        Iterator<Class<?>> iterator = classSet.iterator();
        while(iterator.hasNext()){
            Class clazz = iterator.next();
            Service service = new Service();
            //设置服务开放端口
            service.setPort(8989);
            String fullclassname = clazz.getName();
            //设置完整类名
            service.setClassName(fullclassname);
            RpcService rpcService = (RpcService) clazz.getAnnotation(RpcService.class);
            //获取通过注解配置的服务名
            service.setServiceName(rpcService.value());
            Socket socket = new Socket();
            try {
                socket.connect(new InetSocketAddress("localhost", 9090));
                RegRequest request = new RegRequest();
                //设置状态码
                request.setCode(STATUS.REGISTER);
                request.setObject(service);
                System.out.println("注册服务:"+JSON.toJSON(service)+"");
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.write(new JsonSerializer().serialize(request));
                //关闭输出流
                outputStream.close();
                //关闭链接
                socket.close();
            }catch (IOException e){
                System.out.println("注册服务失败");
                e.printStackTrace();
            }

        }

    }
}
