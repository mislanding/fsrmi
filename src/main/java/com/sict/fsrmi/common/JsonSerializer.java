package com.sict.fsrmi.common;

import com.alibaba.fastjson.JSON;

/**
 * @author lyy
 * @date 2020年10月1日
 * 使用的默认序列化方法
 */
public class JsonSerializer implements Serializer{
    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONBytes(object);
    }
    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(bytes, clazz);
    }
}
