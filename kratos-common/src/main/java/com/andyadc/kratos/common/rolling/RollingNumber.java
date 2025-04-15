package com.andyadc.kratos.common.rolling;

import com.andyadc.kratos.common.util.Pair;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 环形数字类，统计时间的重要类，参考Dubbo实现
 */
public class RollingNumber {

    /**
     * 当前实际时间类
     */
    private static final Time ACTUAL_TIME = new ActualTime();
    /**
     * 环形列表,内部由ListState(环形)存储Buckets
     */
    public final BucketCircularArray buckets;
    /**
     * 每个桶的时间窗口大小
     */
    final long bucketSizeInMillseconds;
    /**
     * 唯一标识：uniqueKey
     */
    final String uniqueKey;
    /**
     * 当前时间戳
     */
    private final Time time;
    /**
     * 消耗总时间
     */
    private final long timeInMilliseconds;
    /**
     * 桶数量
     */
    private final long numberOfBuckets;
    /**
     * 聚合对象
     */
    private final CumulativeSum cumulativeSum;
    private final ReentrantLock newBucketLock = new ReentrantLock();

    /**
     * 构造方法
     */
    public RollingNumber(long timeInMilliseconds, int numberOfBuckets, String uniqueKey, BlockingQueue<Pair<String, Long>> blockingQueue) {
        this(ACTUAL_TIME, timeInMilliseconds, numberOfBuckets, uniqueKey, blockingQueue);
    }

    /**
     * 构造方法
     */
    public RollingNumber(Time time, long timeInMilliseconds, int numberOfBuckets, String uniqueKey,
                         BlockingQueue<Pair<String, Long>> blockingQueue) {
        cumulativeSum = new CumulativeSum(uniqueKey, blockingQueue);
        this.time = time;
        this.timeInMilliseconds = timeInMilliseconds;
        this.numberOfBuckets = numberOfBuckets;

        if (timeInMilliseconds % numberOfBuckets != 0) {
            throw new IllegalArgumentException("The timeInMilliseconds must divide equally into numberOfBuckets. For example 1000/10 is ok, 1000/11 is not.");
        }
        //	一个窗口的Size大小
        this.bucketSizeInMillseconds = timeInMilliseconds / numberOfBuckets;
        //	创建一个数组
        buckets = new BucketCircularArray(numberOfBuckets);
        this.uniqueKey = uniqueKey;
    }

    public void increment(RollingNumberEvent type) {
        getCurrentBucket().getAdder(type).increment();
    }

    public void recordRT(int rt) {
        LongAdder longAdder = getCurrentBucket().getRTBottle().computeIfAbsent(rt, k -> new LongAdder());
        longAdder.increment();
    }

    public void add(RollingNumberEvent type, long value) {
        getCurrentBucket().getAdder(type).add(value);
    }

    public void updateRollingMax(RollingNumberEvent type, long value) {
        getCurrentBucket().getMaxUpdater(type).update(value);
    }

    //	TODO:
    public void reset() {
        Bucket lastBucket = buckets.peekLast();
        if (lastBucket != null) {
            //	CumulativeSum进行统计
            cumulativeSum.addBucket(lastBucket);
        }

        // 	清空数组操作
        buckets.clear();
    }

    public long getCumulativeSum(RollingNumberEvent type) {
        return getValueOfLatestBucket(type) + cumulativeSum.get(type);
    }

    public long getRollingSum(RollingNumberEvent type) {
        Bucket lastBucket = getCurrentBucket();
        if (lastBucket == null)
            return 0;

        long sum = 0;
        for (Bucket b : buckets) {
            sum += b.getAdder(type).sum();
        }
        return sum;
    }

    public long getValueOfLatestBucket(RollingNumberEvent type) {
        Bucket lastBucket = getCurrentBucket();
        if (lastBucket == null)
            return 0;
        return lastBucket.get(type);
    }

    public long[] getValues(RollingNumberEvent type) {
        Bucket lastBucket = getCurrentBucket();
        if (lastBucket == null)
            return new long[0];

        //	获取整个数组bucketArray:
        Bucket[] bucketArray = buckets.getArray();

        long[] values = new long[bucketArray.length];
        int i = 0;
        //	按照类型进行累计
        for (Bucket bucket : bucketArray) {
            if (type.isCounter()) {
                values[i++] = bucket.getAdder(type).sum();
            } else if (type.isMaxUpdater()) {
                values[i++] = bucket.getMaxUpdater(type).max();
            }
        }
        return values;
    }

    public long getRollingMaxValue(RollingNumberEvent type) {
        long[] values = getValues(type);
        if (values.length == 0) {
            return 0;
        }
        //	将其排序获取最大的值
        Arrays.sort(values);
        return values[values.length - 1];
    }

    public Bucket getCurrentBucket() {

        long currentTime = time.getCurrentTimeInMillis();

        Bucket currentBucket = buckets.peekLast();
        if (currentBucket != null && currentTime < currentBucket.windowStart + this.bucketSizeInMillseconds) {
            return currentBucket;
        }

        if (newBucketLock.tryLock()) {
            try {
                if (buckets.peekLast() == null) {
                    Bucket newBucket = new Bucket(currentTime);
                    buckets.addLast(newBucket);
                    return newBucket;
                } else {
                    for (int i = 0; i < numberOfBuckets; i++) {
                        Bucket lastBucket = buckets.peekLast();
                        if (currentTime < lastBucket.windowStart + this.bucketSizeInMillseconds) {
                            return lastBucket;
                        } else if (currentTime - (lastBucket.windowStart + this.bucketSizeInMillseconds) > timeInMilliseconds) {
                            reset();
                            return getCurrentBucket();
                        } else {
                            buckets.addLast(new Bucket(lastBucket.windowStart + this.bucketSizeInMillseconds));
                            cumulativeSum.addBucket(lastBucket);
                        }
                    }
                    return buckets.peekLast();
                }
            } finally {
                newBucketLock.unlock();
            }
        } else {
            currentBucket = buckets.peekLast();
            if (currentBucket != null) {
                return currentBucket;
            } else {
                try {
                    Thread.sleep(5);
                } catch (Exception e) {
                    // ignore
                }
                return getCurrentBucket();
            }
        }
    }
}
