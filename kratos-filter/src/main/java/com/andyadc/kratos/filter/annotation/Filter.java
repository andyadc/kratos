package com.andyadc.kratos.filter.annotation;

/**
 * 过滤器注解
 */
public @interface Filter {

    /**
     * 过滤器唯一id
     */
    String id() default "";

    /**
     * 过滤器名称
     */
    String name() default "";

}
