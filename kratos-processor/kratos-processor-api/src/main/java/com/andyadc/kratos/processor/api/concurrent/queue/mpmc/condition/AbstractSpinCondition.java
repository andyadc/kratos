package com.andyadc.kratos.processor.api.concurrent.queue.mpmc.condition;

import com.andyadc.kratos.processor.api.concurrent.queue.mpmc.thread.ThreadWait;

/**
 * 自旋抽象Condition
 */
public abstract class AbstractSpinCondition implements Condition {

    @Override
    public void awaitNanos(long timeout) throws InterruptedException {
        long currentTime = System.nanoTime();
        final long expireTime = currentTime + timeout;
        final Thread currentThread = Thread.currentThread();
        while (checkCondition() && expireTime > currentTime && !currentThread.isInterrupted()) {
            currentTime = System.nanoTime();
            ThreadWait.onSpinWait();
        }
        if (currentThread.isInterrupted()) {
            throw new InterruptedException(currentThread.getName() + " is interrupted...");
        }
    }

    @Override
    public void await() throws InterruptedException {
        final Thread currentThread = Thread.currentThread();
        while (checkCondition() && !currentThread.isInterrupted()) {
            ThreadWait.onSpinWait();
        }
        if (currentThread.isInterrupted()) {
            throw new InterruptedException(currentThread.getName() + " is interrupted...");
        }
    }

    @Override
    public void signalAll() {
    }

}
