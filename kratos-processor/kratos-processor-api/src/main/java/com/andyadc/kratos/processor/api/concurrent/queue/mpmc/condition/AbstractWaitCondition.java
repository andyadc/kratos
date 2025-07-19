package com.andyadc.kratos.processor.api.concurrent.queue.mpmc.condition;

import com.andyadc.kratos.common.constants.Constants;
import com.andyadc.kratos.processor.api.concurrent.queue.mpmc.thread.ThreadWait;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.LockSupport;

/**
 * 抽象等待Condition
 */
public abstract class AbstractWaitCondition implements Condition {

    private static final int MAX_WAITERS = 8;

    private static final long WAITER_MASK = MAX_WAITERS - 1L;

    private static final long WAIT_TIME_OUT = ThreadWait.TIMEOUT;

    private final LongAdder waitThreads = new LongAdder();

    private final AtomicReferenceArray<Thread> waiterReference =
            new AtomicReferenceArray<>(MAX_WAITERS + 2 * Constants.CACHE_LINE_REFS);

    private long waitCacheCount = 0L;

    @Override
    public abstract boolean checkCondition();

    @Override
    public void awaitNanos(long timeout) throws InterruptedException {
        while (true) {
            try {
                final long waitThreadsSum = this.waitThreads.sum();
                long tempWaitThreads = waitThreadsSum;
                this.waitThreads.increment();
                this.waitCacheCount = waitThreadsSum + 1;

                long currentTime = System.nanoTime();
                final long exprireTime = currentTime + timeout;
                final Thread currentThread = Thread.currentThread();

                // 如果是第一个线程
                if (waitThreadsSum == 0) {
                    int wait = 0;
                    while (checkCondition() && exprireTime > currentTime && !currentThread.isInterrupted()) {
                        wait = ThreadWait.yield(wait);
                        currentTime = System.nanoTime();
                    }
                    if (currentThread.isInterrupted()) {
                        throw new InterruptedException(currentThread.getName() + " is interrupted...");
                    }
                    return;
                } else {
                    int wait = 0;
                    while (checkCondition() && !waiterReference.compareAndSet((int) (tempWaitThreads++ & WAITER_MASK) + Constants.CACHE_LINE_REFS, null, currentThread) && exprireTime > currentTime) {
                        if (wait < ThreadWait.MAX_YIELD) {
                            wait = ThreadWait.yield(wait);
                        } else {
                            LockSupport.parkNanos(MAX_WAITERS * ThreadWait.TIMEOUT);
                        }
                        currentTime = System.nanoTime();
                    }
                    while (checkCondition() && (waiterReference.get((int) ((tempWaitThreads - 1) & WAITER_MASK) + Constants.CACHE_LINE_REFS) == currentThread) && exprireTime > currentTime && !currentThread.isInterrupted()) {
                        LockSupport.parkNanos((exprireTime - currentTime) >> 2);
                        currentTime = System.nanoTime();
                    }
                    if (currentThread.isInterrupted()) {
                        while (!waiterReference.compareAndSet((int) ((tempWaitThreads - 1) & WAITER_MASK) + Constants.CACHE_LINE_REFS, currentThread, null) && waiterReference.get(Constants.CACHE_LINE_REFS) == currentThread) {
                            LockSupport.parkNanos(ThreadWait.TIMEOUT);
                        }
                        throw new InterruptedException(currentThread.getName() + " is interrupted...");
                    }
                    return;
                }
            } finally {
                waitThreads.decrement();
                waitCacheCount = waitThreads.sum();
            }
        }
    }

    @Override
    public void await() throws InterruptedException {
        while (true) {
            try {
                final long waitThreadsSum = this.waitThreads.sum();
                long tempWaitThreads = waitThreadsSum;
                this.waitThreads.increment();
                this.waitCacheCount = waitThreadsSum + 1;

                final Thread currentThread = Thread.currentThread();

                if (waitThreadsSum == 0) {
                    int wait = 0;
                    while (checkCondition() && !currentThread.isInterrupted()) {
                        wait = ThreadWait.yield(wait);
                    }
                    if (currentThread.isInterrupted()) {
                        throw new InterruptedException(currentThread.getName() + " is interrupted...");
                    }
                    return;
                } else {
                    int wait = 0;
                    while (checkCondition() && !waiterReference.compareAndSet((int) (tempWaitThreads++ & WAITER_MASK) + Constants.CACHE_LINE_REFS, null, currentThread) && !currentThread.isInterrupted()) {
                        if (wait < ThreadWait.MAX_YIELD) {
                            wait = ThreadWait.yield(wait);
                        } else {
                            LockSupport.parkNanos(MAX_WAITERS * ThreadWait.TIMEOUT);
                        }
                    }
                    while (checkCondition() && (waiterReference.get((int) ((tempWaitThreads - 1) & WAITER_MASK) + Constants.CACHE_LINE_REFS) == currentThread) && !currentThread.isInterrupted()) {
                        LockSupport.parkNanos(Constants.DEFAULT_PARK_TIMEOUT);
                    }
                    if (currentThread.isInterrupted()) {
                        while (!waiterReference.compareAndSet((int) ((tempWaitThreads - 1) & WAITER_MASK) + Constants.CACHE_LINE_REFS, currentThread, null) && waiterReference.get(Constants.CACHE_LINE_REFS) == currentThread) {
                            LockSupport.parkNanos(WAIT_TIME_OUT);
                        }
                        throw new InterruptedException(currentThread.getName() + " is interrupted...");
                    }
                    return;
                }
            } finally {
                waitThreads.decrement();
                waitCacheCount = waitThreads.sum();
            }
        }
    }

    @Override
    public void signalAll() {
        if (waitCacheCount > 0 || (waitCacheCount = waitThreads.sum()) > 0) {
            long tempWaitThreads = 0L;
            while (true) {
                Thread currentThread;
                while ((currentThread = waiterReference.get((int) (tempWaitThreads++ & WAITER_MASK) + Constants.CACHE_LINE_REFS)) != null) {
                    if (waiterReference.compareAndSet((int) ((tempWaitThreads - 1) & WAITER_MASK) + Constants.CACHE_LINE_REFS, currentThread, null)) {
                        LockSupport.unpark(currentThread);
                    } else {
                        LockSupport.parkNanos(WAIT_TIME_OUT);
                    }

                    if (((tempWaitThreads & WAITER_MASK) == WAITER_MASK) || (waitCacheCount = waitThreads.sum()) == 0) {
                        return;
                    }
                }
                if (((tempWaitThreads & WAITER_MASK) == WAITER_MASK) || (waitCacheCount = waitThreads.sum()) == 0) {
                    return;
                }
            }
        }
    }

}
