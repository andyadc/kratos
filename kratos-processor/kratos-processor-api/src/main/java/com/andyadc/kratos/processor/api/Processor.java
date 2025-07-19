package com.andyadc.kratos.processor.api;

import com.andyadc.kratos.context.config.GatewayConfig;
import com.andyadc.kratos.context.request.HttpGatewayRequestWrapper;
import com.andyadc.kratos.spi.annotation.SPI;

/**
 * 处理器接口
 */
@SPI
public interface Processor {

    /**
     * 初始化处理器
     */
    default void init(GatewayConfig gatewayConfig, Processor processor) {
    }

    /**
     * 处理器的核心方法
     */
    void process(HttpGatewayRequestWrapper httpRequestWrapper) throws Exception;

    /**
     * 启动方法
     */
    void start();

    /**
     * 停止方法
     */
    void shutdown();

}
