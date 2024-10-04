package com.andyadc.kratos.context.ctx;

import com.andyadc.kratos.context.attribute.AttributeKey;
import com.andyadc.kratos.context.rule.Rule;
import io.netty.channel.ChannelHandlerContext;

import java.util.function.Consumer;

/**
 * 网关上下文接口
 */
public interface GatewayContext extends Context {

    /**
     * 获取当前请求的协议，http/rpc/websocket等
     */
    String getProtocol();

    /**
     * 获取当前请求的规则
     */
    Rule getRule();

    /**
     * 获取请求的对象
     */
    Object getRequest();

    /**
     * 获取响应对象
     */
    Object getResponse();

    /**
     * 设置响应对象
     */
    void setResponse(Object response);

    /**
     * 获取异常信息
     */
    Throwable getThrowable();

    /**
     * 由于网关是异步执行任务，如果触发异常，需要将异常封装到上下文
     */
    void setThrowable(Throwable throwable);

    /**
     * 获取网关上下文属性对应的泛型对象
     */
    <T> T getAttribute(AttributeKey<T> key);

    /**
     * 添加网关上下文属性对应的泛型对象
     */
    <T> T putAttribute(AttributeKey<T> key, T value);

    /**
     * 获取网关使用的Netty上下文信息
     */
    ChannelHandlerContext getNettyContext();

    /**
     * 判断当前请求是否保持连接
     */
    boolean isKeepAlive();

    /**
     * 释放请求的资源
     */
    void releaseRequest();

    /**
     * 设置当前请求执行的回调函数
     */
    void setCallback(Consumer<GatewayContext> callback);

    /**
     * 执行回调函数
     */
    void invokeCallback();
}
