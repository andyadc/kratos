package com.andyadc.kratos.register.etcd;

import com.andyadc.kratos.common.thread.NamedThreadFactory;
import com.andyadc.kratos.context.definition.ServiceDefinition;
import com.andyadc.kratos.context.definition.ServiceInstance;
import com.andyadc.kratos.register.api.RegistryService;
import com.andyadc.kratos.register.api.config.RegistryConfig;
import com.andyadc.kratos.register.api.listener.RegistryChangeListener;
import com.andyadc.kratos.register.api.listener.RegistryListener;
import com.andyadc.kratos.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SPIClass
public class EtcdRegistryService implements RegistryService {

    private static final Logger logger = LoggerFactory.getLogger(EtcdRegistryService.class);

    private RegistryConfig registryConfig;
    private ScheduledExecutorService scheduledThreadPool;
    private RegistryListener registryListener;

    @Override
    public void init(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
        this.registryListener = new RegistryChangeListener();
        this.scheduledThreadPool = Executors.newScheduledThreadPool(1, new NamedThreadFactory("RegistryService-Etcd"));
    }

    @Override
    public void register(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) {

    }

    @Override
    public void deregister(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) {

    }

    @Override
    public void discoveryAll() {
        this.discoveryAllService();
        scheduledThreadPool.scheduleWithFixedDelay(this::discoveryAllService, 10, 10, TimeUnit.SECONDS);
    }

    private void discoveryAllService() {

    }

    @Override
    public void destroy() {
        this.scheduledThreadPool.shutdown();
    }
}
