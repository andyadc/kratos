package com.andyadc.kratos.common.rolling;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * 桶实现类
 */
public class Bucket {

    /**
     * 窗口开始时间
     */
    final long windowStart;
    /**
     * 存储事件，累积新增
     */
    final LongAdder[] adderForCounterType;
    /**
     * 存储事件，累积更新
     */
    final LongMaxUpdater[] updaterForCounterType;

    /**
     * 存储RT时间
     */
    final ConcurrentHashMap<Integer, LongAdder> rtBottle;

    Bucket(long startTime) {
        this.windowStart = startTime;
        adderForCounterType = new LongAdder[RollingNumberEvent.values().length];
        for (RollingNumberEvent type : RollingNumberEvent.values()) {
            if (type.isCounter()) {
                adderForCounterType[type.ordinal()] = new LongAdder();
            }
        }
        updaterForCounterType = new LongMaxUpdater[RollingNumberEvent.values().length];
        for (RollingNumberEvent type : RollingNumberEvent.values()) {
            if (type.isMaxUpdater()) {
                updaterForCounterType[type.ordinal()] = new LongMaxUpdater();
                // initialize to 0 otherwise it is Long.MIN_VALUE
                updaterForCounterType[type.ordinal()].update(0);
            }
        }
        rtBottle = new ConcurrentHashMap<>();
    }

    long get(RollingNumberEvent type) {
        if (type.isCounter()) {
            return adderForCounterType[type.ordinal()].sum();
        }
        if (type.isMaxUpdater()) {
            return updaterForCounterType[type.ordinal()].max();
        }
        throw new IllegalStateException("Unknown type of event: " + type.name());
    }

    LongAdder getAdder(RollingNumberEvent type) {
        if (!type.isCounter()) {
            throw new IllegalStateException("Type is not a Counter: " + type.name());
        }
        return adderForCounterType[type.ordinal()];
    }

    LongMaxUpdater getMaxUpdater(RollingNumberEvent type) {
        if (!type.isMaxUpdater()) {
            throw new IllegalStateException("Type is not a MaxUpdater: " + type.name());
        }
        return updaterForCounterType[type.ordinal()];
    }

    //	返回Map集合rtBottle
    ConcurrentHashMap<Integer, LongAdder> getRTBottle() {
        return rtBottle;
    }

    long getWindowStart() {
        return windowStart;
    }

}
