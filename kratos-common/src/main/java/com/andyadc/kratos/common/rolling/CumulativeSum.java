package com.andyadc.kratos.common.rolling;

import com.andyadc.kratos.common.util.Pair;
import com.andyadc.kratos.common.util.StringUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * 累计和
 */
public class CumulativeSum {

    /**
     * 事件集合数组, 累积增量
     */
    private final LongAdder[] adderForCounterType;
    /**
     * 事件集合数组, 累积更新
     */
    private final LongMaxUpdater[] updaterForCounterType;

    private final String uniqueKey;

    private final BlockingQueue<Pair<String, Long>> blockingQueue;

    CumulativeSum(String uniqueKey, BlockingQueue<Pair<String, Long>> blockingQueue) {

        this.uniqueKey = uniqueKey;
        this.blockingQueue = blockingQueue;

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
    }

    public void addBucket(Bucket lastBucket) {
        for (RollingNumberEvent type : RollingNumberEvent.values()) {
            if (type.isCounter()) {
                long sum = lastBucket.getAdder(type).sum();
                getAdder(type).add(sum);
                if (sum != 0) {
                    if (blockingQueue != null) {
                        blockingQueue.add(new Pair<String, Long>(type.name(), sum));
                    }
                }
            }
            if (type.isMaxUpdater()) {
                long max = lastBucket.getMaxUpdater(type).max();
                getMaxUpdater(type).update(max);
                if (max != 0) {
                    if (blockingQueue != null) {
                        blockingQueue.add(new Pair<String, Long>(type.name(), max));
                    }
                }
            }
        }
        String rtValue = rtBottleToString(lastBucket.getRTBottle());
        if (StringUtils.isNotEmpty(rtValue)) {
            //	每次上报rt数据的时机
        }
    }

    private String rtBottleToString(ConcurrentHashMap<Integer, LongAdder> bottle) {
        if (bottle == null || bottle.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        bottle.forEach((key, value) -> sb.append(key).append(":").append(value.longValue()).append(","));
        if ((",").equals(sb.substring(sb.length() - 1, sb.length()))) {
            sb.delete(sb.length() - 1, sb.length());
        }
        return sb.toString();
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

}
