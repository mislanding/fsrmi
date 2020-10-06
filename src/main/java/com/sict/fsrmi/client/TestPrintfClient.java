/**
 * @author lyy
 * @data 2020/10/3
 * @version 1.0
 */
package com.sict.fsrmi.client;
import com.sict.fsrmi.common.Logger;

import java.util.Arrays;


public class TestPrintfClient {
    public static void main(String[] args) {
        Logger logger =  (Logger) RpcProxyClient.getProxy(new com.sict.fsrmi.client.Logger());
        int[] strs = new int[1025];
        Arrays.fill(strs, 100);
        System.out.println(logger.printf("%s%d", "wlke", 6,strs));

        System.out.println(logger.printf("kkk"));
    }
}
