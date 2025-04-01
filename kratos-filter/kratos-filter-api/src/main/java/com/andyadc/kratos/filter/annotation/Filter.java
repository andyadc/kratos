package com.andyadc.kratos.filter.annotation;

import com.andyadc.kratos.common.enums.FilterType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 过滤器注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Filter {

    /**
     * 过滤器唯一id
     */
    String id() default "";

    /**
     * 过滤器名称
     */
    String name() default "";

    /**
     * 标识过滤器的类型
     */
    FilterType value();

    /**
     * 排序，按从小到大顺序依次执行
     */
    int order() default 0;

}
