package com.sict.fsrmi.server.annotation;

import java.lang.annotation.*;

/**
 * @author lyy
 * @date 2020年10月3日
 * 标注注册到注册中心的服务，表明其可被远程进行调用,并在服务端启动时创建实体
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcService{
    public String value();
}
