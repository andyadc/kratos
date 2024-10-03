package com.andyadc.kratos.context.ctx;

/**
 * 上下文顶层接口，主要维护网关接收请求和响应数据的生命周期
 */
public interface Context {

    /**
     * 请求正在执行中
     */
    int RUNNING = 1;

    /**
     * 处理完请求，需要写回响应标记
     */
    int WRITTEN = 2;

    /**
     * 响应数据成功的状态
     */
    int COMPLETED = 3;

    /**
     * 请求与响应结束
     */
    int TERMINATED = 4;

    /**
     * 设置状态为运行中
     */
    void running();

    /**
     * 设置状态为处理完请求，需要写回响应标记
     */
    void written();

    /**
     * 设置状态为响应数据成功的状态
     */
    void completed();

    /**
     * 设置状态为请求与响应结束
     */
    void terminated();

    /**
     * 判断状态是否为运行中
     */
    boolean isRunning();

    /**
     * 判断状态是否为需要写回响应标记
     */
    boolean isWritten();

    /**
     * 判断状态是否为响应数据成功的状态
     */
    boolean isCompleted();

    /**
     * 判断状态是否为请求与响应结束
     */
    boolean isTerminated();

    /**
     * 网关服务端接收请求的时间
     */
    long getServerReceiveRequestTime();

    /**
     * 设置网关服务端接收请求时间
     */
    void setServerReceiveRequestTime(long serverReceiveRequestTime);

    /**
     * 获取网关服务端发送响应时间
     */
    long getServerSendResponseTime();

    /**
     * 设置网关服务端发送响应时间
     */
    void setServerSendResponseTime(long serverSendResponseTime);

    /**
     * 获取网关客户端发送请求时间
     */
    long getClientSendRequestTime();

    /**
     * 设置网关客户端发送请求时间
     */
    void setClientSendRequestTime(long clientSendRequestTime);

    /**
     * 获取网关客户端接收响应时间
     */
    long getClientReceiveResponseTime();

    /**
     * 设置网关客户端接收响应时间
     */
    void setClientReceiveResponseTime(long clientReceiveResponseTime);
}
