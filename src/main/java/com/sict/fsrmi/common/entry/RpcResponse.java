package com.sict.fsrmi.common.entry;

import com.sict.fsrmi.common.JsonSerializer;
import lombok.Data;

/**
 * @author lyy
 * @date 2020年9月30日
 * 规定通信协议
 */
@Data
public class RpcResponse extends JsonSerializer {
    /**
     * 响应ID，确定响应与请求对应
     */
    private String requestId;
    /**
     * 错误信息
     */
    private String error;
    /**
     * 结果
     */
    private Object result;
}
