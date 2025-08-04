package com.andyadc.kratos.register.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingMaintainFactory;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;
import com.alibaba.nacos.common.executor.NameThreadFactory;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.andyadc.kratos.common.constants.Constants;
import com.andyadc.kratos.common.exception.RegistryException;
import com.andyadc.kratos.common.util.JsonUtils;
import com.andyadc.kratos.context.definition.ServiceDefinition;
import com.andyadc.kratos.context.definition.ServiceInstance;
import com.andyadc.kratos.register.api.RegistryService;
import com.andyadc.kratos.register.api.config.RegistryConfig;
import com.andyadc.kratos.register.api.listener.RegistryChangeListener;
import com.andyadc.kratos.register.api.listener.RegistryListener;
import com.andyadc.kratos.register.nacos.listener.NacosEventListener;
import com.andyadc.kratos.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SPIClass
public class NacosRegistryService implements RegistryService {

    private static final Logger logger = LoggerFactory.getLogger(NacosRegistryService.class);

    private RegistryConfig registryConfig;
    private NamingService namingService;
    private NamingMaintainService namingMaintainService;
    private ScheduledExecutorService scheduledThreadPool;
    private RegistryListener registryListener;

    @Override
    public void init(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
        this.registryListener = new RegistryChangeListener();
        try {
            this.namingService = NamingFactory.createNamingService(registryConfig.getRegistryAddress());
            this.namingMaintainService = NamingMaintainFactory.createMaintainService(registryConfig.getRegistryAddress());
            this.scheduledThreadPool = Executors.newScheduledThreadPool(1, new NameThreadFactory("RegistryService-Nacos"));
        } catch (NacosException e) {
            throw new RegistryException(e);
        }
    }

    @Override
    public void register(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) {
        try {
            Instance instance = new Instance();
            instance.setInstanceId(serviceInstance.getInstanceId());
            instance.setPort(serviceInstance.getPort());
            instance.setIp(serviceInstance.getAddress());
            instance.setMetadata(Map.of(Constants.META_DATA, JsonUtils.toJSONString(serviceInstance)));
            // 服务注册
            namingService.registerInstance(serviceDefinition.getServiceId(), registryConfig.getEnv(), instance);

            // 服务定义信息
            namingMaintainService.updateService(serviceDefinition.getServiceId(), registryConfig.getEnv(), 0,
                    Map.of(Constants.META_DATA, JsonUtils.toJSONString(serviceDefinition)));

            logger.info("Service register: {}, {}", serviceDefinition, serviceInstance);
        } catch (NacosException e) {
            throw new RegistryException(e);
        }
    }

    @Override
    public void deregister(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) {
        try {
            namingService.deregisterInstance(
                    serviceDefinition.getServiceId(),
                    registryConfig.getEnv(),
                    serviceInstance.getAddress(),
                    serviceInstance.getPort()
            );
        } catch (NacosException e) {
            throw new RegistryException(e);
        }
    }

    @Override
    public void discoveryAll() {
        this.discoveryAllService();
        scheduledThreadPool.scheduleWithFixedDelay(this::discoveryAllService, 10, 10, TimeUnit.SECONDS);
    }

    private void discoveryAllService() {
        try {
            Set<String> serviceSet = namingService.getSubscribeServices().stream().map(ServiceInfo::getName).collect(Collectors.toSet());
            // 分页拉取服务实例信息
            int currentPageNo = 1;
            int pageSize = 100;
            List<String> serviceList = namingService.getServicesOfServer(currentPageNo, pageSize, registryConfig.getEnv()).getData();
            while (CollectionUtils.isNotEmpty(serviceList)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("service list size: {}", serviceList.size());
                }
                for (String service : serviceList) {
                    if (serviceSet.contains(service)) {
                        continue;
                    }
                    EventListener eventListener = new NacosEventListener(registryListener, namingService, namingMaintainService, registryConfig);
                    eventListener.onEvent(new NamingEvent(service, null));
                    namingService.subscribe(service, registryConfig.getEnv(), eventListener);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Service subscribe: {}, {}", service, registryConfig.getEnv());
                    }
                }
                serviceList = namingService.getServicesOfServer(++currentPageNo, pageSize, registryConfig.getEnv()).getData();
            }
        } catch (NacosException e) {
            throw new RegistryException(e);
        }
    }

    @Override
    public void destroy() {
        this.scheduledThreadPool.shutdown();
    }

}
