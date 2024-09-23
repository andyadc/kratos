package com.andyadc.kratos.context.request;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * 请求包装类
 */
public class HttpGatewayRequestWrapper {

    /**
     * 真正发出请求的对象
     */
    private FullHttpRequest fullHttpRequest;

    /**
     * Netty中的上下文对象
     */
    private ChannelHandlerContext ctx;

    public HttpGatewayRequestWrapper() {
    }

    public HttpGatewayRequestWrapper(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {
        this.fullHttpRequest = fullHttpRequest;
        this.ctx = ctx;
    }

    public FullHttpRequest getFullHttpRequest() {
        return fullHttpRequest;
    }

    public void setFullHttpRequest(FullHttpRequest fullHttpRequest) {
        this.fullHttpRequest = fullHttpRequest;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
}
