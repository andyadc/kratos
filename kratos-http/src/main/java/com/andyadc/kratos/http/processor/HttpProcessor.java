package com.andyadc.kratos.http.processor;

/**
 * Http管理器接口
 */
public interface HttpProcessor {

    /**
     * 初始化方法
     */
    void init();

    /**
     * 启动方法
     */
    void start();

    /**
     * 停止方法
     */
    void shutdown();
}
