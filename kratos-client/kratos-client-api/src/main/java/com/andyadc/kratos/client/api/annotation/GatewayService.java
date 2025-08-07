package com.andyadc.kratos.client.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GatewayService {

    /**
     * 服务id
     */
    String serviceId();

    /**
     * 版本号
     */
    String version() default "1.0.0";

    /**
     * 协议，http/rpc/websocket
     * Protocol.HTTP/Protocol.RPC/Protocol.WEBSOCKET
     */
    String protocol();

    /**
     * 匹配的路径
     */
    String patternPath();
}
