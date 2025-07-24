package com.andyadc.kratos.register.api;

import com.andyadc.kratos.context.definition.ServiceDefinition;
import com.andyadc.kratos.context.definition.ServiceInstance;
import com.andyadc.kratos.register.api.config.RegistryConfig;
import com.andyadc.kratos.spi.annotation.SPI;

@SPI
public interface RegistryService {

    /**
     * 初始化
     */
    default void init(RegistryConfig registryConfig) {
    }

    /**
     * 服务注册
     */
    void register(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance);

    /**
     * 服务反注册
     */
    void deregister(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance);

    /**
     * 服务发现
     */
    void discoveryAll();

    /**
     * 关闭
     */
    default void destroy() {
    }

}
