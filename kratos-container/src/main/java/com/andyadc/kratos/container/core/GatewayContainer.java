package com.andyadc.kratos.container.core;

import com.andyadc.kratos.common.enums.ProcessorType;
import com.andyadc.kratos.container.Container;
import com.andyadc.kratos.container.client.HttpClientContainer;
import com.andyadc.kratos.container.server.HttpServerContainer;
import com.andyadc.kratos.context.config.GatewayConfig;
import com.andyadc.kratos.context.definition.ServiceDefinition;
import com.andyadc.kratos.context.definition.ServiceInstance;
import com.andyadc.kratos.context.definition.factory.ServiceFactory;
import com.andyadc.kratos.processor.api.Processor;
import com.andyadc.kratos.processor.api.netty.NettyCoreProcessor;
import com.andyadc.kratos.register.api.RegistryService;
import com.andyadc.kratos.register.api.config.RegistryConfig;
import com.andyadc.kratos.spi.loader.ExtensionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 启动容器类
 */
public class GatewayContainer implements Container {

    private static final Logger logger = LoggerFactory.getLogger(GatewayContainer.class);

    private final GatewayConfig gatewayConfig;
    private Processor processor;
    private HttpServerContainer httpServerContainer;
    private HttpClientContainer httpClientContainer;
    private RegistryService registryService;
    private ServiceDefinition serviceDefinition;
    private ServiceInstance serviceInstance;

    public GatewayContainer(GatewayConfig gatewayConfig) {
        this.gatewayConfig = gatewayConfig;
        //调用初始化方法
        this.init();
    }

    @Override
    public void init() {
        long start = System.currentTimeMillis();
        Processor coreProcessor = new NettyCoreProcessor();
        String processorType = gatewayConfig.getProcessorType();
        if (ProcessorType.isBatchEventOrMpmc(processorType)) {
            // 使用SPI加载BatchEvent处理器或者MPMC处理器
            this.processor = ExtensionLoader.getExtension(Processor.class, processorType);
            this.processor.init(gatewayConfig, coreProcessor);
        } else {
            // 直接赋值核心处理器
            this.processor = coreProcessor;
        }
        // 实例化HttpServer容器
        this.httpServerContainer = new HttpServerContainer(this.gatewayConfig, this.processor);
        // 实例化HttpClient容器
        this.httpClientContainer = new HttpClientContainer(this.gatewayConfig, this.httpServerContainer.getEventLoopGroupWork());
        // SPI加载注册中心
        this.registryService = ExtensionLoader.getExtension(RegistryService.class, gatewayConfig.getRegistryType());
        this.registryService.init(new RegistryConfig(gatewayConfig.getRegistryAddress(), gatewayConfig.getEnv()));
        // 生成网关服务定义信息
        this.serviceDefinition = ServiceFactory.getKratosServiceDefinition(gatewayConfig);
        // 生成网关服务实例信息
        this.serviceInstance = ServiceFactory.getKratosServiceInstance(gatewayConfig);
        logger.info("Kratos GatewayContainer initialized in {} ms", (System.currentTimeMillis() - start));
    }

    @Override
    public void start() {
        long start = System.currentTimeMillis();
        this.processor.start();
        this.httpServerContainer.start();
        this.httpClientContainer.start();
        // 服务注册
        this.registryService.register(serviceDefinition, serviceInstance);
        // 服务发现
        this.registryService.discoveryAll();
        logger.info("Kratos GatewayContainer started in {} ms", (System.currentTimeMillis() - start));
    }

    @Override
    public void shutdown() {
        // 服务反注册
        this.registryService.deregister(serviceDefinition, serviceInstance);
        // 注册中心销毁
        this.registryService.destroy();
        this.httpClientContainer.shutdown();
        this.httpServerContainer.shutdown();
        this.processor.shutdown();
        logger.info("Kratos GatewayContainer shutdown completed.");
    }

}
