package com.andyadc.kratos.filter.post.statistics;

import com.andyadc.kratos.common.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 消费数据，上报统计指标
 */
public class StatisticsConsumer implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(StatisticsConsumer.class);

    private ArrayBlockingQueue<Pair<String, Long>> statisticsQueue = new ArrayBlockingQueue<>(65535);

    private volatile boolean isRunning = false;

    public void start() {
        isRunning = true;
    }

    public void shutdown() {
        isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                Pair<String, Long> pair = statisticsQueue.take();
                String key = pair.getT1();
                Long value = pair.getT2();
                // 这里为了简化实现，打印下日志，实际会封装指标进行上报
                if (logger.isDebugEnabled()) {
                    logger.debug("upload metric statistics number, key: {}, value: {}", key, value);
                }
            } catch (InterruptedException e) {
                logger.warn("InterruptedException ", e);
            }
        }
    }

    public ArrayBlockingQueue<Pair<String, Long>> getStatisticsQueue() {
        return statisticsQueue;
    }

    public void setStatisticsQueue(ArrayBlockingQueue<Pair<String, Long>> statisticsQueue) {
        this.statisticsQueue = statisticsQueue;
    }

    public boolean isRunning() {
        return isRunning;
    }

}
