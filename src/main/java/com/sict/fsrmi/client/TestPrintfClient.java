/**
 * @author lyy
 * @data 2020/10/3
 * @version 1.0
 */
package com.sict.fsrmi.client;
import com.sict.fsrmi.common.Logger;



public class TestPrintfClient {
    public static void main(String[] args) {
        //获得代理类
        Logger logger =  (Logger) RpcProxyClient.getProxy(new com.sict.fsrmi.client.Logger());
        //通过代理类进行调用
        String result = logger.printf("多次调用测试");
        System.out.println(result);
    }
}
