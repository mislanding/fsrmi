/**
 * @author lyy
 * @data 2020/10/3
 * @version 1.0
 */
package com.sict.fsrmi.register;

import lombok.Data;

@Data
public class Service {
    /**
     * 对应服务所在IP
     */
    private String ip;
    /**
     * 对应服务所在端口号
     */
    private int port;
    /**
     * 服务名称，用于远程调用
     */
    private String serviceName;
    /**
     * 服务对应类实体的完整类名
     */
    private String className;
}
