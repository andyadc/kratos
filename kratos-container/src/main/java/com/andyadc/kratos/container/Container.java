package com.andyadc.kratos.container;

public interface Container {

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
