package com.sict.fsrmi.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lyy
 * @date 2020年10月3日
 * 标注注册到注册中心的服务，表明其可被远程进行调用
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {
    String value();
}
