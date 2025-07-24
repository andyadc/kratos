package com.andyadc.kratos.register.api.config;

import java.io.Serial;
import java.io.Serializable;

public class RegistryConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -53015894973787339L;

    /**
     * 注册中心地址
     */
    private String registryAddress;
    /**
     * 注册环境
     */
    private String env;

    public RegistryConfig() {
    }

    public RegistryConfig(String registryAddress, String env) {
        this.registryAddress = registryAddress;
        this.env = env;
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    @Override
    public String toString() {
        return "RegistryConfig{" +
                "registryAddress=" + registryAddress +
                ", env=" + env +
                '}';
    }

}
