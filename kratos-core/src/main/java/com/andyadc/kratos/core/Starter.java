package com.andyadc.kratos.core;

import com.andyadc.kratos.container.core.GatewayContainer;
import com.andyadc.kratos.context.config.GatewayConfig;
import com.andyadc.kratos.context.loader.GatewayConfigLoader;

public class Starter {

    public static void main(String[] args) {
        // 加载配置
        GatewayConfig gatewayConfig = GatewayConfigLoader.getInstance().loadConfig(args);
        // 初始化容器
        GatewayContainer container = new GatewayContainer(gatewayConfig);
        // 启动容器
        container.start();
        // 优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread(container::shutdown));
    }

}
