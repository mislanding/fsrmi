package com.sict.fsrmi.common.entry;

import lombok.Data;

/**
 * @author lyy
 * @version 1.0
 * @data 2020/10/4
 */
@Data
public class RegRequest {
    /**
     * 消息类型
     */
    private String code;
    /**
     * 消息对象
     */
    private Object object;
}
