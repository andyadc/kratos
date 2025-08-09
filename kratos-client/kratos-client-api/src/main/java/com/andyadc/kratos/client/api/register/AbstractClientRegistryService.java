package com.andyadc.kratos.client.api.register;

import com.andyadc.kratos.client.api.properties.GatewayClientProperties;
import com.andyadc.kratos.client.api.scanner.GatewayClassScanner;
import com.andyadc.kratos.common.constants.Constants;
import com.andyadc.kratos.common.exception.RegistryException;
import com.andyadc.kratos.common.time.SystemClock;
import com.andyadc.kratos.common.util.IPUtils;
import com.andyadc.kratos.context.definition.ServiceDefinition;
import com.andyadc.kratos.context.definition.ServiceInstance;
import com.andyadc.kratos.register.api.RegistryService;
import com.andyadc.kratos.register.api.config.RegistryConfig;
import com.andyadc.kratos.spi.loader.ExtensionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractClientRegistryService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractClientRegistryService.class);
    private final GatewayClientProperties gatewayClientProperties;
    private final RegistryService registryService;

    protected AbstractClientRegistryService(GatewayClientProperties gatewayClientProperties) {
        this.gatewayClientProperties = gatewayClientProperties;
        this.registryService = ExtensionLoader.getExtension(RegistryService.class, gatewayClientProperties.getRegistryType());
        if (registryService == null) {
            logger.error("未获取到对应的注册中心实现类, registryType:{}", gatewayClientProperties.getRegistryType());
            throw new RegistryException("未获取到对应的注册中心实现类");
        }
        this.registryService.init(new RegistryConfig(gatewayClientProperties.getRegistryAddress(), gatewayClientProperties.getEnv()));
    }

    /**
     * 服务注册逻辑
     */
    protected void registerWithDeregisterOnShutdownHook(Object bean, int port, Object... args) {
        ServiceDefinition serviceDefinition = GatewayClassScanner.scanAndGetServiceDefinition(bean, args);
        if (serviceDefinition == null) {
            return;
        }
        serviceDefinition.setEnvType(gatewayClientProperties.getEnv());
        ServiceInstance serviceInstance = new ServiceInstance();
        String ip = IPUtils.getLocalIp();
        String uniqueId = serviceDefinition.getUniqueId();
        String version = serviceDefinition.getVersion();
        serviceInstance.setInstanceId(ip + Constants.COLON_SEPARATOR + port);
        serviceInstance.setUniqueId(uniqueId);
        serviceInstance.setAddress(ip);
        serviceInstance.setPort(port);
        serviceInstance.setRegisterTime(SystemClock.millisClock().now());
        serviceInstance.setVersion(version);
        serviceInstance.setWeight(Constants.WEIGHT);
        serviceInstance.setGray(gatewayClientProperties.isGray());
        // 服务注册
        this.registryService.register(serviceDefinition, serviceInstance);

        // 服务反注册
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.registryService.deregister(serviceDefinition, serviceInstance);
        }));
    }
}
