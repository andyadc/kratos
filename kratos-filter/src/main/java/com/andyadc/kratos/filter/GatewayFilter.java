package com.andyadc.kratos.filter;

/**
 * 核心过滤器接口
 */
public interface GatewayFilter<T> {

    /**
     * 校验是否执行当前过滤器的方法
     */
    boolean check(T t) throws Exception;

    /**
     * 执行当前过滤器的方法
     */
    void execute(T t, Object... args) throws Exception;

    /**
     * 触发下一个过滤器的执行
     */
    void nextFilter(T t, Object... args) throws Exception;

    /**
     * 执行对象方法
     */
    void executeEntry(T t, Object... args) throws Exception;

    /**
     * 初始化方法，不做强制实现处理
     */
    default void initFilter() throws Exception {
    }

    /**
     * 销毁方法，不做强制实现处理
     */
    default void destroyFilter() throws Exception {
    }

    /**
     * 刷新过滤器，不做强制实现处理
     */
    default void refreshFilter() throws Exception {
    }
}