package com.andyadc.kratos.processor.api.concurrent.queue.batchevent.event;

/**
 * BatchEvent接口定义
 *
 * @param <T>
 */
public interface BatchEvent<T> {

    /**
     * 向队列添加元素
     */
    void add(T event);

    /**
     * 添加多个元素
     */
    void add(T... events);

    /**
     * 尝试添加元素, 如果添加成功返回true 失败返回false
     */
    boolean tryAdd(T event);

    /**
     * 尝试添加多个元素, 如果添加成功返回true 失败返回false
     */
    boolean tryAdd(T... events);

    /**
     * 是否已停止
     */
    boolean isShutdown();

    /**
     * 启动
     */
    void start();

    /**
     * 停止
     */
    void shutdown();

}
