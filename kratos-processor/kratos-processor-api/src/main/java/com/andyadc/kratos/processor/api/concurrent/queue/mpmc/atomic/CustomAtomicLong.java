package com.andyadc.kratos.processor.api.concurrent.queue.mpmc.atomic;

import com.andyadc.kratos.common.constants.Constants;

import java.util.concurrent.atomic.AtomicLongArray;

/**
 * 自定义AtomicLong
 */
public class CustomAtomicLong {

    /**
     * 缓存行可以存储多少个long类型的数据
     */
    private static final int LONG_CACHE_LINE = Constants.CACHE_LINE / Long.BYTES;

    private final AtomicLongArray atomicLongArray;

    public CustomAtomicLong(final long num) {
        this.atomicLongArray = new AtomicLongArray(2 * LONG_CACHE_LINE);
        this.set(num);
    }

    public void set(final long num) {
        this.atomicLongArray.set(LONG_CACHE_LINE, num);
    }

    public long get() {
        return atomicLongArray.get(LONG_CACHE_LINE);
    }

    public boolean compareAndSet(final long expectedValue, final long newValue) {
        return this.atomicLongArray.compareAndSet(LONG_CACHE_LINE, expectedValue, newValue);
    }

    @Override
    public String toString() {
        return Long.toString(this.get());
    }

}
