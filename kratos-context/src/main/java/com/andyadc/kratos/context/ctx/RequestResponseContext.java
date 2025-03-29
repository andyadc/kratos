package com.andyadc.kratos.context.ctx;

import com.andyadc.kratos.common.util.AssertUtils;
import com.andyadc.kratos.context.attribute.AttributeKey;
import com.andyadc.kratos.context.request.HttpGatewayRequest;
import com.andyadc.kratos.context.response.GatewayResponse;
import com.andyadc.kratos.context.rule.FilterRule;
import com.andyadc.kratos.context.rule.Rule;
import io.micrometer.core.instrument.Timer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

/**
 * 请求与响应上下文
 */
public class RequestResponseContext extends AbstractContext {

    private final HttpGatewayRequest request;
    private final Rule rule;
    private GatewayResponse response;
    private boolean gray;

    private Timer.Sample timerSample;

    public RequestResponseContext(String protocol,
                                  ChannelHandlerContext nettyContext,
                                  boolean keepAlive,
                                  HttpGatewayRequest request,
                                  Rule rule) {
        super(protocol, nettyContext, keepAlive);
        this.request = request;
        this.rule = rule;
    }

    public static RequestResponseContext getRequestResponseContext(String protocol,
                                                                   ChannelHandlerContext nettyContext,
                                                                   boolean keepAlive,
                                                                   HttpGatewayRequest request,
                                                                   Rule rule) {
        AssertUtils.notNull(protocol, "protocol不能为空");
        AssertUtils.notNull(nettyContext, "nettyCtx不能为空");
        AssertUtils.notNull(request, "rapidRequest不能为空");
        AssertUtils.notNull(rule, "rule不能为空");
        return new RequestResponseContext(protocol, nettyContext, keepAlive, request, rule);
    }

    @Override
    public Rule getRule() {
        return this.rule;
    }

    @Override
    public Object getRequest() {
        return this.request;
    }

    @Override
    public Object getResponse() {
        return this.response;
    }

    @Override
    public void setResponse(Object response) {
        this.response = (GatewayResponse) response;
    }

    @Override
    public <T> T getRequiredAttribute(AttributeKey<T> key) {
        T value = getAttribute(key);
        AssertUtils.notNull(value, "获取的上下文属性为空, 此时的key为: " + key);
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAttributeOrDefault(AttributeKey<T> key, T defaultValue) {
        return (T) attributes.getOrDefault(key, defaultValue);
    }

    @Override
    public FilterRule getFilterRule(String filterId) {
        return rule.getFilterRule(filterId);
    }

    @Override
    public String getUniqueId() {
        return request.getUniqueId();
    }

    /**
     * 重写覆盖父类的方法，真正释放资源
     */
    @Override
    public void releaseRequest() {
        if (requestReleased.compareAndSet(false, true)) {
            ReferenceCountUtil.release(request.getFullHttpRequest());
        }
    }

    @Override
    public HttpGatewayRequest getOriginRequest() {
        return request;
    }

    @Override
    public HttpGatewayRequest getModifyRequest() {
        return request;
    }

    @Override
    public boolean isGray() {
        return gray;
    }

    @Override
    public void setGray(boolean gray) {
        this.gray = gray;
    }

    @Override
    public Timer.Sample getTimerSample() {
        return timerSample;
    }

    @Override
    public void setTimerSample(Timer.Sample timerSample) {
        this.timerSample = timerSample;
    }

}
