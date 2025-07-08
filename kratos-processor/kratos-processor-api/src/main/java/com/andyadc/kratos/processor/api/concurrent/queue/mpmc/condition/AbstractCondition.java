package com.andyadc.kratos.processor.api.concurrent.queue.mpmc.condition;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 自定义抽象Contition
 */
public abstract class AbstractCondition implements Condition {

    // 可重入锁
    private final Lock lock = new ReentrantLock();
    // 并发编程中的Condition
    private final java.util.concurrent.locks.Condition condition = lock.newCondition();

    @Override
    public void awaitNanos(final long timeout) throws InterruptedException {
        long tempTimeout = timeout;
        lock.lock();
        try {
            while (checkCondition() && tempTimeout > 0) {
                tempTimeout = condition.awaitNanos(tempTimeout);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void await() throws InterruptedException {
        lock.lock();
        try {
            while (checkCondition()) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void signalAll() {
        lock.lock();
        try {
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

}
