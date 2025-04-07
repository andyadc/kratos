package com.andyadc.kratos.context.ctx;

import com.andyadc.kratos.context.attribute.AttributeKey;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * 上下文抽象类
 */
public abstract class AbstractContext implements GatewayContext {

    /**
     * 请求协议，标注是http/rpc/websocket等
     */
    protected final String protocol;

    /**
     * NettyContext
     */
    protected final ChannelHandlerContext nettyContext;

    /**
     * 是否保持连接
     */
    protected final boolean keepAlive;

    /**
     * 存储上下文集合参数
     */
    protected final Map<AttributeKey<?>, Object> attributes = new ConcurrentHashMap<>();

    /**
     * 存储String为key的上下文集合参数
     */
    protected final Map<String, Object> attributeStringMapping = new ConcurrentHashMap<>();

    /**
     * 是否已经释放请求的资源标识
     */
    protected final AtomicBoolean requestReleased = new AtomicBoolean(false);

    /**
     * 网关服务端接收请求的时间
     */
    protected long serverReceiveRequestTime;

    /**
     * 网关服务端发送响应的时间
     */
    protected long serverSendResponseTime;

    /**
     * 网关客户端发送请求的时间
     */
    protected long clientSendRequestTime;

    /**
     * 网关客户端接收响应的时间
     */
    protected long clientReceiveResponseTime;

    /**
     * 请求上下文的状态
     */
    protected volatile int status = Context.RUNNING;

    /**
     * 主要存储请求时的异常信息
     */
    protected Throwable throwable;

    /**
     * 存放所有的回调函数
     */
    protected List<Consumer<GatewayContext>> callbacks;

    public AbstractContext(String protocol, ChannelHandlerContext nettyContext, boolean keepAlive) {
        this.protocol = protocol;
        this.keepAlive = keepAlive;
        this.nettyContext = nettyContext;
    }

    @Override
    public String getProtocol() {
        return this.protocol;
    }

    @Override
    public ChannelHandlerContext getNettyContext() {
        return this.nettyContext;
    }

    @Override
    public boolean isKeepAlive() {
        return this.keepAlive;
    }

    @Override
    public void running() {
        this.status = Context.RUNNING;
    }

    @Override
    public void written() {
        this.status = Context.WRITTEN;
    }

    @Override
    public void completed() {
        this.status = Context.COMPLETED;
    }

    @Override
    public void terminated() {
        this.status = Context.TERMINATED;
    }

    @Override
    public boolean isRunning() {
        return this.status == Context.RUNNING;
    }

    @Override
    public boolean isWritten() {
        return this.status == Context.WRITTEN;
    }

    @Override
    public boolean isCompleted() {
        return this.status == Context.COMPLETED;
    }

    @Override
    public boolean isTerminated() {
        return this.status == Context.TERMINATED;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAttribute(AttributeKey<T> key) {
        return (T) attributes.get(key);
    }

    @Override
    public <T> T getAttribute(String key) {
        return (T) attributeStringMapping.get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T putAttribute(AttributeKey<T> key, T value) {
        return (T) attributes.put(key, value);
    }

    @Override
    public <T> T putAttribute(String key, T value) {
        return (T) attributeStringMapping.put(key, value);
    }

    @Override
    public Throwable getThrowable() {
        return this.throwable;
    }

    @Override
    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public void releaseRequest() {
        this.requestReleased.compareAndSet(false, true);
    }

    @Override
    public void setCallback(Consumer<GatewayContext> callback) {
        if (this.callbacks == null) {
            this.callbacks = new ArrayList<>();
        }
        this.callbacks.add(callback);
    }

    @Override
    public void invokeCallback() {
        if (this.callbacks != null) {
            this.callbacks.forEach(call -> call.accept(this));
        }
    }

    @Override
    public long getServerReceiveRequestTime() {
        return this.serverReceiveRequestTime;
    }

    @Override
    public void setServerReceiveRequestTime(long serverReceiveRequestTime) {
        this.serverReceiveRequestTime = serverReceiveRequestTime;
    }

    @Override
    public long getServerSendResponseTime() {
        return this.serverSendResponseTime;
    }

    @Override
    public void setServerSendResponseTime(long serverSendResponseTime) {
        this.serverSendResponseTime = serverSendResponseTime;
    }

    @Override
    public long getClientSendRequestTime() {
        return this.clientSendRequestTime;
    }

    @Override
    public void setClientSendRequestTime(long clientSendRequestTime) {
        this.clientSendRequestTime = clientSendRequestTime;
    }

    @Override
    public long getClientReceiveResponseTime() {
        return this.clientReceiveResponseTime;
    }

    @Override
    public void setClientReceiveResponseTime(long clientReceiveResponseTime) {
        this.clientReceiveResponseTime = clientReceiveResponseTime;
    }
}
