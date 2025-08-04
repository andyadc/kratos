package com.andyadc.kratos.register.nacos.listener;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.Service;
import com.andyadc.kratos.common.constants.Constants;
import com.andyadc.kratos.common.exception.RegistryException;
import com.andyadc.kratos.common.util.JsonUtils;
import com.andyadc.kratos.context.definition.ServiceDefinition;
import com.andyadc.kratos.context.definition.ServiceInstance;
import com.andyadc.kratos.register.api.config.RegistryConfig;
import com.andyadc.kratos.register.api.listener.RegistryListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Nacos事件监听器
 */
public class NacosEventListener implements EventListener {

    private final RegistryListener registryListener;
    private final NamingService namingService;
    private final NamingMaintainService namingMaintainService;
    private final RegistryConfig registryConfig;

    public NacosEventListener(RegistryListener registryListener, NamingService namingService,
                              NamingMaintainService namingMaintainService, RegistryConfig registryConfig) {
        this.registryListener = registryListener;
        this.namingService = namingService;
        this.namingMaintainService = namingMaintainService;
        this.registryConfig = registryConfig;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof NamingEvent namingEvent) {
            String serviceName = namingEvent.getServiceName();
            try {
                // 服务定义信息
                Service service = namingMaintainService.queryService(serviceName, registryConfig.getEnv());
                ServiceDefinition serviceDefinition = JsonUtils.parse(service.getMetadata().get(Constants.META_DATA), ServiceDefinition.class);

                // 服务实例信息
                List<Instance> allInstances = namingService.getAllInstances(service.getName(), registryConfig.getEnv());
                Set<ServiceInstance> serviceInstanceSet = new HashSet<>();
                for (Instance instance : allInstances) {
                    ServiceInstance serviceInstance = JsonUtils.parse(instance.getMetadata().get(Constants.META_DATA), ServiceInstance.class);
                    serviceInstanceSet.add(serviceInstance);
                }
                registryListener.onListener(serviceDefinition, serviceInstanceSet);
            } catch (NacosException e) {
                throw new RegistryException(e);
            }
        }
    }

}
