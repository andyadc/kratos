package com.andyadc.kratos.context.invoker;

/**
 * 服务调用顶层接口
 */
public interface GatewayInvoker {

    /**
     * 获取服务调用的全路径信息
     */
    String getInvokerPath();

    /**
     * 设置服务调用的全链路信息
     */
    void setInvokerPath(String invokerPath);

    /**
     * 获取超时时间
     */
    int getTimeout();

    /**
     * 设置超时时间
     */
    void setTimeout(int timeout);
}
