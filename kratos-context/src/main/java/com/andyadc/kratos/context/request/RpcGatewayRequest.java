package com.andyadc.kratos.context.request;

/**
 * RPC请求对象
 */
public interface RpcGatewayRequest extends GatewayRequest {

    /**
     * 获取服务注册地址
     */
    String getRegistryAddress();

    /**
     * 设置服务注册地址
     */
    void setRegistryAddress(String registryAddress);

    /**
     * 获取接口class名称
     */
    String getInterfaceClass();

    /**
     * 设置接口class名称
     */
    void setInterfaceClass(String interfaceClass);

    /**
     * 获取方法名称
     */
    String getMethodName();

    /**
     * 设置方法名称
     */
    void setMethodName(String methodName);

    /**
     * 获取参数类型数组
     */
    String[] getParameterTypes();

    /**
     * 设置参数类型数组
     */
    void setParameterTypes(String[] parameterTypes);

    /**
     * 获取参数数组
     */
    Object[] getArgs();

    /**
     * 设置参数数组
     */
    void setArgs(Object[] args);

    /**
     * 获取超时时间
     */
    int getTimeout();

    /**
     * 设置超时时间
     */
    void setTimeout(int timeout);

    /**
     * 获取版本号
     */
    String getVersion();

    /**
     * 设置版本号
     */
    void setVersion(String version);
}
