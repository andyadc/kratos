package com.andyadc.kratos.context.request;

public class DefaultRpcGatewayRequest implements RpcGatewayRequest {

    /**
     * RPC服务注册地址
     */
    private String registryAddress;
    /**
     * 服务的接口名称
     */
    private String interfaceClass;
    /**
     * 服务的方法名
     */
    private String methodName;
    /**
     * 服务的方法参数签名
     */
    private String[] parameterTypes;
    /**
     * 调用的参数内容
     */
    private Object[] args;
    /**
     * 调用的超时时间
     */
    private int timeout;
    /**
     * 调用的版本号
     */
    private String version;

    @Override
    public String getRegistryAddress() {
        return this.registryAddress;
    }

    @Override
    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    @Override
    public String getInterfaceClass() {
        return this.interfaceClass;
    }

    @Override
    public void setInterfaceClass(String interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public String getMethodName() {
        return this.methodName;
    }

    @Override
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String[] getParameterTypes() {
        return this.parameterTypes;
    }

    @Override
    public void setParameterTypes(String[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    @Override
    public Object[] getArgs() {
        return this.args;
    }

    @Override
    public void setArgs(Object[] args) {
        this.args = args;
    }

    @Override
    public int getTimeout() {
        return this.timeout;
    }

    @Override
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

}
