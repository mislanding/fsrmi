/**
 * @author lyy
 * @data 2020/10/3
 * @version 1.0
 */
package com.sict.fsrmi.client;
import com.sict.fsrmi.common.Logger;


public class TestPrintfClient {
    public static void main(String[] args) {
        Logger logger =  (Logger) RpcProxyClient.getProxy(new com.sict.fsrmi.client.Logger());
        System.out.println(logger.printf(5));
    }
}
