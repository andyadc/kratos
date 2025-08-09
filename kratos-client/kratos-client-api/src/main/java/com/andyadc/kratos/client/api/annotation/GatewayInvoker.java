package com.andyadc.kratos.client.api.annotation;

import java.lang.annotation.*;

/**
 * 方法级别注解，主要执行的是方法的逻辑
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GatewayInvoker {

    /**
     * 规则id，与nacos配置的规则id对应
     */
    String ruleId();

    /**
     * 接口的路径
     */
    String path();
}
