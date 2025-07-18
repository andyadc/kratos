package com.andyadc.kratos.register.api.config;

import java.io.Serial;
import java.io.Serializable;

public class RegistryConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -53015894973787339L;

    /**
     * 注册中心地址
     */
    private String registerAddress;
    /**
     * 注册环境
     */
    private String env;

    public String getRegisterAddress() {
        return registerAddress;
    }

    public void setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress;
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
                "registerAddress=" + registerAddress +
                ", env=" + env +
                '}';
    }

}
