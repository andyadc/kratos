package com.andyadc.kratos.client.api.properties;

public class GatewayClientProperties {

    private String registryAddress = "127.0.0.1:8765";

    /**
     * 注册中心类型，取值为：nacosRegistry/zookeeperRegistry/etcdRegistry/consulRegistry
     * 通过SPI加载对应的注册中心实现类
     */
    private String registryType = "nacosRegistry";

    private String env = "dev";

    private boolean gray = false;

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public String getRegistryType() {
        return registryType;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public boolean isGray() {
        return gray;
    }

    public void setGray(boolean gray) {
        this.gray = gray;
    }

}
