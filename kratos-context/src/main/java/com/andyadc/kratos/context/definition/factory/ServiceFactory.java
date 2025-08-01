package com.andyadc.kratos.context.definition.factory;

import com.andyadc.kratos.common.constants.Constants;
import com.andyadc.kratos.common.time.SystemClock;
import com.andyadc.kratos.common.util.IPUtils;
import com.andyadc.kratos.context.config.GatewayConfig;
import com.andyadc.kratos.context.definition.ServiceDefinition;
import com.andyadc.kratos.context.definition.ServiceInstance;

import java.util.Collections;

/**
 * 服务工厂类
 */
public class ServiceFactory {

    /**
     * 生成服务定义信息
     */
    public static ServiceDefinition getPolarisServiceDefinition(GatewayConfig gatewayConfig) {
        ServiceDefinition serviceDefinition = new ServiceDefinition();
        serviceDefinition.setUniqueId(gatewayConfig.getGatewayName().concat(Constants.COLON_SEPARATOR).concat(gatewayConfig.getVersion()));
        serviceDefinition.setServiceId(gatewayConfig.getGatewayName());
        serviceDefinition.setEnvType(gatewayConfig.getEnv());
        serviceDefinition.setVersion(gatewayConfig.getVersion());
        serviceDefinition.setPatternPath(Constants.PATH_PATTERN);
        serviceDefinition.setInvokerMap(Collections.emptyMap());
        return serviceDefinition;
    }

    /**
     * 生成服务实例信息
     */
    public static ServiceInstance getPolarisServiceInstance(GatewayConfig gatewayConfig) {
        String localIp = IPUtils.getLocalIp();
        int port = gatewayConfig.getPort();
        ServiceInstance serviceInstance = new ServiceInstance();
        String instanceId = localIp.concat(Constants.COLON_SEPARATOR).concat(String.valueOf(port));
        serviceInstance.setInstanceId(instanceId);
        serviceInstance.setUniqueId(gatewayConfig.getGatewayName().concat(Constants.COLON_SEPARATOR).concat(gatewayConfig.getVersion()));
        serviceInstance.setAddress(localIp);
        serviceInstance.setPort(port);
        serviceInstance.setWeight(gatewayConfig.getWeight());
        serviceInstance.setRegisterTime(SystemClock.millisClock().now());
        serviceInstance.setVersion(gatewayConfig.getVersion());
        serviceInstance.setGray(gatewayConfig.isGray());
        return serviceInstance;
    }

}
