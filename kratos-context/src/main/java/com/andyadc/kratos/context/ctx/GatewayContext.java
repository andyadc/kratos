package com.andyadc.kratos.context.ctx;

import com.andyadc.kratos.context.attribute.AttributeKey;
import com.andyadc.kratos.context.request.HttpGatewayRequest;
import com.andyadc.kratos.context.rule.FilterRule;
import com.andyadc.kratos.context.rule.Rule;
import io.micrometer.core.instrument.Timer;
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
    <T> T getAttribute(String key);

    /**
     * 获取网关上下文属性对应的泛型对象
     */
    <T> T getAttribute(AttributeKey<T> key);

    /**
     * 添加网关上下文属性对应的泛型对象
     */
    <T> T putAttribute(String key, T value);

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

    /**
     * 获取上下文属性
     */
    <T> T getRequiredAttribute(AttributeKey<T> key);

    /**
     * 获取上下文属性
     */
    <T> T getAttributeOrDefault(AttributeKey<T> key, T defaultValue);

    /**
     * 根据id获取过滤规则
     */
    FilterRule getFilterRule(String filterId);

    /**
     * 获取唯一id
     */
    String getUniqueId();

    /**
     * 获取原始请求
     */
    HttpGatewayRequest getOriginRequest();

    /**
     * 获取可修改的请求
     */
    HttpGatewayRequest getModifyRequest();

    /**
     * 判断是否是灰度
     */
    boolean isGray();

    /**
     * 设置灰度标识
     */
    void setGray(boolean gray);

    /**
     * 获取时间
     */
    Timer.Sample getTimerSample();

    /**
     * 设置时间
     */
    void setTimerSample(Timer.Sample timerSample);

}
