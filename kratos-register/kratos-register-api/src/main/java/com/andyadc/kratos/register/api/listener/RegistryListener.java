package com.andyadc.kratos.register.api.listener;

import com.andyadc.kratos.context.definition.ServiceDefinition;
import com.andyadc.kratos.context.definition.ServiceInstance;

import java.util.Set;

/**
 * 注册监听器接口
 */
public interface RegistryListener {

    /**
     * 监听变化
     */
    void onListener(ServiceDefinition serviceDefinition, Set<ServiceInstance> serviceInstanceSet);

}
