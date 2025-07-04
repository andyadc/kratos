package com.andyadc.kratos.processor.api.concurrent.queue.mpmc.thread;

import com.andyadc.kratos.processor.api.concurrent.queue.mpmc.condition.Condition;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * ThreadWait实现
 */
public class ThreadWait {

    public static final long TIMEOUT = 50;
    public static final int MIN_YIELD = 500;
    public static final int MIDDLE_YIELD = 1000;
    public static final int MAX_YIELD = 2000;

    public static int yield(final int n) {
        if (n > MIN_YIELD) {
            if (n < MIDDLE_YIELD) {
                if ((n & 0x7) == 0) {
                    LockSupport.parkNanos(TIMEOUT);
                } else {
                    onSpinWait();
                }
            } else if (n < MAX_YIELD) {
                if ((n & 0x3) == 0) {
                    Thread.yield();
                } else {
                    onSpinWait();
                }
            } else {
                Thread.yield();
                return n;
            }
        } else {
            onSpinWait();
        }
        return n + 1;
    }

    // 参考Thread类的onSpinWait
    // java.lang.Thread.onSpinWait
    public static void onSpinWait() {
    }


    public static boolean waitTimeout(final long timeout, final TimeUnit unit, final Condition condition) throws InterruptedException {
        final long timeoutNanos = TimeUnit.NANOSECONDS.convert(timeout, unit);
        final long expireTime = System.nanoTime() + timeoutNanos;
        while (condition.checkCondition()) {
            final long now = System.nanoTime();
            if (now > expireTime) {
                return false;
            }
            condition.awaitNanos(expireTime - now - TIMEOUT);
        }
        return true;
    }

}
