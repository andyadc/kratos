package com.andyadc.kratos.examples;

import com.andyadc.kratos.container.core.GatewayContainer;
import com.andyadc.kratos.context.cache.config.ConfigCacheFactory;
import com.andyadc.kratos.context.config.GatewayConfig;
import com.andyadc.kratos.context.definition.ServiceDefinition;
import com.andyadc.kratos.context.invoker.HttpServiceInvoker;
import com.andyadc.kratos.context.invoker.ServiceInvoker;
import com.andyadc.kratos.context.loader.GatewayConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class HttpExampleGatewayStarter {

    private static final Logger logger = LoggerFactory.getLogger(HttpExampleGatewayStarter.class);

    public static void main(String[] args) {
        // 初始化后端服务相关配置
        initServiceConfig();
        // 启动网关
        startGateway(args);
    }

    private static void startGateway(String[] args) {
        // 加载配置
        GatewayConfig gatewayConfig = GatewayConfigLoader.getInstance().loadConfig(args);
        // 初始化容器
        GatewayContainer gatewayContainer = new GatewayContainer(gatewayConfig);
        // 启动容器
        gatewayContainer.start();
        // 优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread(gatewayContainer::shutdown));
    }

    private static void initServiceConfig() {
        // 接口path
        String path = "/http_test";
        String serviceId = "100001";
        String version = "1.0.0";
        String uniqueId = serviceId + ":" + version;
        String ruleId = "10001";
        String protocol = "http";
        String address = "localhost";
        int port = 8080;

        // 创建ServiceInvoker对象
        ServiceInvoker serviceInvoker = new HttpServiceInvoker();
        serviceInvoker.setInvokerPath(path);
        serviceInvoker.setRuleId(ruleId);
        serviceInvoker.setTimeout(5000);

        // 创建ServiceDefinition对象，后续注册到注册中心
        ServiceDefinition serviceDefinition = new ServiceDefinition();
        serviceDefinition.setServiceId(serviceId);
        serviceDefinition.setVersion(version);
        serviceDefinition.setUniqueId(uniqueId);
        serviceDefinition.setProtocol(protocol);
        serviceDefinition.setPatternPath(path);
        serviceDefinition.setEnvType("local");

        Map<String, ServiceInvoker> invokerMap = new HashMap<>();
        invokerMap.put(path, serviceInvoker);
        serviceDefinition.setInvokerMap(invokerMap);
        ConfigCacheFactory.getInstance().putServiceDefinition(uniqueId, serviceDefinition);

    }

}
