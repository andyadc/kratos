package com.andyadc.kratos.filter.base;

import com.andyadc.kratos.context.ctx.GatewayContext;
import com.andyadc.kratos.exector.response.ResponseHelper;
import com.andyadc.kratos.filter.GatewayFilter;

/**
 * 抽象链表过滤器
 */
public abstract class AbstractGatewayFilter<T> implements GatewayFilter<GatewayContext> {

    /**
     * 下一个过滤器
     */

    protected AbstractGatewayFilter<T> nextFilter;

    @Override
    public void nextFilter(GatewayContext context, Object... args) throws Exception {
        // 如果已经完成，则不再向下执行
        if (context.isCompleted()) {
            return;
        }
        // 向客户端发送响应结果
        if (context.isWritten()) {
            ResponseHelper.sendResponse(context);
        }

        // 已经没有下一个过滤器
        if (nextFilter == null) {
            context.terminated();
            return;
        }
        // 如果存在下一个过滤器
        // 如果执行当前过滤器
        if (nextFilter.check(context)) {
            nextFilter.transform(context, args);
        } else {
            nextFilter.nextFilter(context, args);
        }
    }

    @Override
    public void transform(GatewayContext context, Object... args) throws Exception {
        // 触发真正执行过滤器的方法
        execute(context, args);
    }

    public AbstractGatewayFilter<T> getNextFilter() {
        return nextFilter;
    }

    public void setNextFilter(AbstractGatewayFilter<T> nextFilter) {
        this.nextFilter = nextFilter;
    }

}
