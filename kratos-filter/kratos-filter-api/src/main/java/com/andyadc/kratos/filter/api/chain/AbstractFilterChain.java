package com.andyadc.kratos.filter.api.chain;

import com.andyadc.kratos.filter.api.base.AbstractGatewayFilter;

/**
 * 抽象过滤器链
 *
 * @param <T>
 */
public abstract class AbstractFilterChain<T> extends AbstractGatewayFilter<T> {

    /**
     * 添加元素到元素头部
     */
    public abstract void addFirst(AbstractGatewayFilter<T> filter);

    /**
     * 添加元素到链路尾部
     */
    public abstract void addLast(AbstractGatewayFilter<T> filter);

}
