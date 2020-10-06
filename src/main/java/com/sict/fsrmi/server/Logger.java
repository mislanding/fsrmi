
package com.sict.fsrmi.server;

import com.sict.fsrmi.server.annotation.RpcService;

import java.util.Arrays;

/**
 * @author lyy
 * @data 2020/10/3
 * @version 1.0
 */
@RpcService(value ="Logger")
public class Logger implements com.sict.fsrmi.common.Logger {
    @Override
    public String printf(String str) {
        if (str.length() > 1024) {
            return "false,字符串长度大于1024字节";
        }
        System.out.println(str);
        return "success";
    }

    @Override
    public String printf(String str, Object... args) {
        Object[] objects = args;
        if (args.length > 1024) {
            return "false,格式化变量数大于1024个";
        }
        for (Object object : objects) {
            if (object.getClass().equals(String.class)) {
                String string = (String) object;
                if (string.length() > 1024) {
                    return "false,字符串长度大于1024字节";
                }
            }
        }
        System.out.println(String.format(str, args));
        return "success";
    }
}
