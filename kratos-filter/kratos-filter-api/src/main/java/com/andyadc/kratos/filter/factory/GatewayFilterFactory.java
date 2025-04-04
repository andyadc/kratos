package com.andyadc.kratos.filter.factory;

import com.andyadc.kratos.common.enums.FilterType;
import com.andyadc.kratos.context.ctx.GatewayContext;
import com.andyadc.kratos.filter.GatewayFilter;

import java.util.List;

/**
 * 过滤器工厂接口
 */
public interface GatewayFilterFactory {

    /**
     * 构造过滤器链
     */
    void buildFilterChain(FilterType filterType, List<GatewayFilter<GatewayContext>> filterList) throws Exception;

    /**
     * 正常执行过滤器链
     */
    void executeFilterChain(GatewayContext context) throws Exception;

    /**
     * 异常和错误时执行过滤器链
     */
    void executeErrorFilterChain(GatewayContext context) throws Exception;

    /**
     * 执行Mock过滤器链
     */
    void executeMockFilterChain(GatewayContext context) throws Exception;

    /**
     * 执行灰度过滤器链
     */
    void executeGrayFilterChain(GatewayContext context) throws Exception;

    /**
     * 获取过滤器
     */
    <T> T getFilter(Class<T> clazz) throws Exception;

    /**
     * 根据id获取过滤器
     */
    <T> T getFilter(String filterId) throws Exception;

}
