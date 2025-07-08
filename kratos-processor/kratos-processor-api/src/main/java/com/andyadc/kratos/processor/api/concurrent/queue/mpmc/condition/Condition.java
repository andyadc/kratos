package com.andyadc.kratos.processor.api.concurrent.queue.mpmc.condition;

/**
 * Condition接口设计，参考JDK并发编程包的Condition
 */
public interface Condition {

    /**
     * 检测是否满足队列条件
     */
    boolean checkCondition();

    /**
     * 超时等待
     */
    void awaitNanos(final long timeout) throws InterruptedException;

    /**
     * 等待
     */
    void await() throws InterruptedException;

    /**
     * 信号量
     */
    void signalAll();

}
