package com.andyadc.kratos.filter.chain;

import com.andyadc.kratos.context.ctx.GatewayContext;
import com.andyadc.kratos.filter.base.AbstractGatewayFilter;

/**
 * 默认过滤器链
 */
public class DefaultFilterChain extends AbstractFilterChain<GatewayContext> {

    private final String id;

    // 过滤器链的头结点
    private final AbstractGatewayFilter<GatewayContext> firstFilter = new AbstractGatewayFilter<>() {

        @Override
        public boolean check(GatewayContext context) throws Exception {
            return true;
        }

        @Override
        public void execute(GatewayContext context, Object... args) throws Exception {
            super.nextFilter(context, args);
        }

    };

    // 过滤器链的尾节点
    private AbstractGatewayFilter<GatewayContext> endFilter = firstFilter;

    public DefaultFilterChain(String id) {
        this.id = id;
    }

    @Override
    public boolean check(GatewayContext gatewayContext) throws Exception {
        return true;
    }

    @Override
    public void execute(GatewayContext gatewayContext, Object... args) throws Exception {
        this.firstFilter.transform(gatewayContext, args);
    }

    @Override
    public void addFirst(AbstractGatewayFilter<GatewayContext> filter) {
        filter.setNextFilter(this.firstFilter.getNextFilter());
        this.firstFilter.setNextFilter(filter);
        if (this.endFilter == this.firstFilter) {
            this.endFilter = filter;
        }
    }

    @Override
    public void addLast(AbstractGatewayFilter<GatewayContext> filter) {
        this.endFilter.setNextFilter(filter);
        this.endFilter = filter;
    }

    @Override
    public AbstractGatewayFilter<GatewayContext> getNextFilter() {
        return this.firstFilter.getNextFilter();
    }

    @Override
    public void setNextFilter(AbstractGatewayFilter<GatewayContext> nextFilter) {
        this.addLast(nextFilter);
    }

    public String getId() {
        return id;
    }

}
