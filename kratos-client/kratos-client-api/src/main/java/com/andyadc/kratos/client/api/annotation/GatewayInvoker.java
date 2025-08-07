package com.andyadc.kratos.client.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法级别注解，主要执行的是方法的逻辑
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GatewayInvoker {

    /**
     * 接口的路径
     */
    String path();
}
