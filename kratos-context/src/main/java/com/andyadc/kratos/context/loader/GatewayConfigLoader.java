package com.andyadc.kratos.context.loader;

import com.andyadc.kratos.context.config.GatewayConfig;

/**
 * 网关配置加载器
 */
public class GatewayConfigLoader {

    private final static GatewayConfigLoader INSTANCE = new GatewayConfigLoader();

    private final GatewayConfig gatewayConfig = new GatewayConfig();

    private GatewayConfigLoader() {
    }

    public static GatewayConfigLoader getInstance() {
        return INSTANCE;
    }

    public static GatewayConfig getGatewayConfig() {
        return INSTANCE.gatewayConfig;
    }

}
