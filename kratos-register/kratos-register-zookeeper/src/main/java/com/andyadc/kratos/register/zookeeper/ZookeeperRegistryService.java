package com.andyadc.kratos.register.zookeeper;

import com.andyadc.kratos.common.exception.RegistryException;
import com.andyadc.kratos.common.thread.NamedThreadFactory;
import com.andyadc.kratos.common.util.CollectionUtils;
import com.andyadc.kratos.context.definition.ServiceDefinition;
import com.andyadc.kratos.context.definition.ServiceInstance;
import com.andyadc.kratos.register.api.RegistryService;
import com.andyadc.kratos.register.api.config.RegistryConfig;
import com.andyadc.kratos.register.api.listener.RegistryChangeListener;
import com.andyadc.kratos.register.api.listener.RegistryListener;
import com.andyadc.kratos.spi.annotation.SPIClass;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SPIClass
public class ZookeeperRegistryService implements RegistryService {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperRegistryService.class);

    private static final int BASE_SLEEP_TIME_MS = 1000;
    private static final int MAX_RETRIES = 3;
    private static final String ZK_BASE_PATH = "/kratos";
    private static final String ZK_DEFINITION_PATH = ZK_BASE_PATH + "/definition";
    private static final String ZK_INSTANCES_PATH = ZK_BASE_PATH + "/instances";
    private ServiceDiscovery<ServiceDefinition> serviceDefinitionDiscovery;
    private ServiceDiscovery<ServiceInstance> serviceInstanceDiscovery;
    private ScheduledExecutorService scheduledThreadPool;
    private RegistryListener registryListener;

    @Override
    public void init(RegistryConfig registryConfig) {
        try {
            this.registryListener = new RegistryChangeListener();
            this.scheduledThreadPool = Executors.newScheduledThreadPool(1, new NamedThreadFactory("RegistryService-Zookeeper"));
            CuratorFramework client = CuratorFrameworkFactory.newClient(registryConfig.getRegistryAddress(), new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
            client.start();
            logger.info("Curator client started.");

            JsonInstanceSerializer<ServiceDefinition> serviceDefinitionSerializer = new JsonInstanceSerializer<>(ServiceDefinition.class);
            this.serviceDefinitionDiscovery = ServiceDiscoveryBuilder.builder(ServiceDefinition.class)
                    .client(client)
                    .serializer(serviceDefinitionSerializer)
                    .basePath(ZK_DEFINITION_PATH)
                    .build();
            this.serviceDefinitionDiscovery.start();
            logger.info("ServiceDefinition basePath: {} started", ZK_DEFINITION_PATH);

            JsonInstanceSerializer<ServiceInstance> serviceInstanceSerializer = new JsonInstanceSerializer<>(ServiceInstance.class);
            this.serviceInstanceDiscovery = ServiceDiscoveryBuilder.builder(ServiceInstance.class)
                    .client(client)
                    .serializer(serviceInstanceSerializer)
                    .basePath(ZK_INSTANCES_PATH)
                    .build();
            this.serviceInstanceDiscovery.start();
            logger.info("ServiceDefinition basePath: {} started.", ZK_INSTANCES_PATH);
        } catch (Exception e) {
            logger.error("ZookeeperRegistry initialization error.", e);
            throw new RegistryException(e);
        }
    }

    @Override
    public void register(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) {
        try {
            // 服务定义信息
            org.apache.curator.x.discovery.ServiceInstance<ServiceDefinition> zookeeperServiceDefinition = org.apache.curator.x.discovery.ServiceInstance
                    .<ServiceDefinition>builder()
                    .id(serviceDefinition.getUniqueId())
                    .name(serviceDefinition.getServiceId())
                    .address(serviceInstance.getAddress())
                    .port(serviceInstance.getPort())
                    .payload(serviceDefinition)
                    .build();
            serviceDefinitionDiscovery.updateService(zookeeperServiceDefinition);
            logger.info("ZookeeperRegistry register definition: {}", serviceDefinition);

            // 服务注册信息
            org.apache.curator.x.discovery.ServiceInstance<ServiceInstance> zookeeperServiceInstance = org.apache.curator.x.discovery.ServiceInstance
                    .<ServiceInstance>builder()
                    .id(serviceInstance.getUniqueId())
                    .name(serviceInstance.getInstanceId())
                    .address(serviceInstance.getAddress())
                    .port(serviceInstance.getPort())
                    .payload(serviceInstance)
                    .build();
            serviceInstanceDiscovery.updateService(zookeeperServiceInstance);
            logger.info("ZookeeperRegistry register instance: {}", serviceInstance);
        } catch (Exception e) {
            logger.error("ZookeeperRegistry register error. {}, {}", serviceDefinition, serviceInstance);
            throw new RegistryException(e);
        }
    }

    @Override
    public void deregister(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) {
        try {
            org.apache.curator.x.discovery.ServiceInstance<ServiceDefinition> zookeeperServiceDefinition = org.apache.curator.x.discovery.ServiceInstance
                    .<ServiceDefinition>builder()
                    .id(serviceDefinition.getUniqueId())
                    .name(serviceDefinition.getServiceId())
                    .address(serviceInstance.getAddress())
                    .port(serviceInstance.getPort())
                    .payload(serviceDefinition)
                    .build();
            serviceDefinitionDiscovery.unregisterService(zookeeperServiceDefinition);
            logger.info("ZookeeperRegistry deregister definition: {}", serviceDefinition);

            org.apache.curator.x.discovery.ServiceInstance<ServiceInstance> zookeeperServiceInstance = org.apache.curator.x.discovery.ServiceInstance
                    .<ServiceInstance>builder()
                    .id(serviceInstance.getUniqueId())
                    .name(serviceInstance.getInstanceId())
                    .address(serviceInstance.getAddress())
                    .port(serviceInstance.getPort())
                    .payload(serviceInstance)
                    .build();
            serviceInstanceDiscovery.unregisterService(zookeeperServiceInstance);

            logger.info("ZookeeperRegistry deregister instance: {}", serviceInstance);
        } catch (Exception e) {
            logger.error("ZookeeperRegistry deregister error. {}, {}", serviceDefinition, serviceInstance);
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
            List<ServiceDefinition> serviceDefinitionList = this.getAllServiceDefinition(serviceDefinitionDiscovery.queryForNames());
            List<ServiceInstance> serviceInstanceList = this.getAllServiceInstance(serviceDefinitionDiscovery.queryForNames());
            serviceDefinitionList.forEach(serviceDefinition -> {
                Set<ServiceInstance> serviceInstanceSet = serviceInstanceList
                        .stream()
                        .filter(serviceInstance -> serviceInstance.getUniqueId().equals(serviceDefinition.getUniqueId()))
                        .collect(Collectors.toSet());
                registryListener.onListener(serviceDefinition, serviceInstanceSet);
            });
        } catch (Exception e) {
            throw new RegistryException(e);
        }
    }

    private List<ServiceDefinition> getAllServiceDefinition(Collection<String> serviceDefinitionNameList) throws Exception {
        if (CollectionUtils.isEmpty(serviceDefinitionNameList)) {
            return Collections.emptyList();
        }
        List<ServiceDefinition> serviceDefinitionList = new ArrayList<>();
        for (String name : serviceDefinitionNameList) {
            serviceDefinitionList.addAll(this.getServiceDefinitionFromServiceInstance(new ArrayList<>(serviceDefinitionDiscovery.queryForInstances(name))));
        }
        return serviceDefinitionList;
    }

    private List<ServiceDefinition> getServiceDefinitionFromServiceInstance(List<org.apache.curator.x.discovery.ServiceInstance<ServiceDefinition>> serviceInstances) {
        List<ServiceDefinition> list = new ArrayList<>();
        if (serviceInstances == null || serviceInstances.isEmpty()) return list;
        IntStream.range(0, serviceInstances.size()).forEach((i) -> {
            org.apache.curator.x.discovery.ServiceInstance<ServiceDefinition> serviceInstance = serviceInstances.get(i);
            list.add(serviceInstance.getPayload());
        });
        return list;
    }

    private List<ServiceInstance> getAllServiceInstance(Collection<String> serviceInstanceNameList) throws Exception {
        if (CollectionUtils.isEmpty(serviceInstanceNameList)) {
            return Collections.emptyList();
        }
        List<ServiceInstance> serviceInstanceList = new ArrayList<>();
        for (String name : serviceInstanceNameList) {
            serviceInstanceList.addAll(this.getServiceInstanceFromServiceInstance(new ArrayList<>(serviceInstanceDiscovery.queryForInstances(name))));
        }
        return serviceInstanceList;
    }

    private List<ServiceInstance> getServiceInstanceFromServiceInstance(List<org.apache.curator.x.discovery.ServiceInstance<ServiceInstance>> serviceInstances) {
        List<ServiceInstance> list = new ArrayList<>();
        if (serviceInstances == null || serviceInstances.isEmpty()) return list;
        IntStream.range(0, serviceInstances.size()).forEach((i) -> {
            org.apache.curator.x.discovery.ServiceInstance<ServiceInstance> serviceInstance = serviceInstances.get(i);
            list.add(serviceInstance.getPayload());
        });
        return list;
    }

    @Override
    public void destroy() {
        try {
            serviceDefinitionDiscovery.close();
            serviceInstanceDiscovery.close();
            this.scheduledThreadPool.shutdown();
            logger.info("ZookeeperRegistry destroy completed.");
        } catch (IOException e) {
            // ignore
            logger.error("ZookeeperRegistry destroy error.", e);
        }
    }

}
