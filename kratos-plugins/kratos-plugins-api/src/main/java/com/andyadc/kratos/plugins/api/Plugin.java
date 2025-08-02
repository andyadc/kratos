package com.andyadc.kratos.plugins.api;

import com.andyadc.kratos.spi.annotation.SPI;

/**
 * 插件接口
 */
@SPI
public interface Plugin {

    /**
     * 检测插件
     */
    default boolean check() {
        return true;
    }

    /**
     * 初始化插件
     */
    void init();

    /**
     * 销毁插件
     */
    void destroy();

    /**
     * 根据插件名称获取插件
     */
    Plugin get(String pluginName);
}
