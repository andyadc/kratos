package com.andyadc.kratos.processor.api.concurrent.queue.batchevent.event;

import com.andyadc.kratos.processor.api.concurrent.queue.batchevent.holder.Holder;
import com.andyadc.kratos.processor.api.concurrent.queue.batchevent.holder.HolderEventFactory;
import com.andyadc.kratos.processor.api.concurrent.queue.batchevent.holder.HolderEventTranslator;
import com.andyadc.kratos.processor.api.concurrent.queue.batchevent.holder.HolderExceptionHandler;
import com.andyadc.kratos.processor.api.concurrent.queue.batchevent.holder.HolderWorkHandler;
import com.andyadc.kratos.processor.api.concurrent.queue.batchevent.listener.EventListener;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 基于disruptor实现的多生产者多消费者工具类
 *
 * @param <T>
 */
public class ParallelBatchEvent<T> implements BatchEvent<T> {

    private final EventListener<T> eventListener;
    private final WorkerPool<Holder<T>> workerPool;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final EventTranslatorOneArg<Holder<T>, T> eventTranslator;
    private RingBuffer<Holder<T>> ringBuffer;

    private ParallelBatchEvent(Builder<T> builder) {
        this.threadPoolExecutor = new ThreadPoolExecutor(
                builder.threadPoolSize,
                builder.threadPoolSize,
                builder.keepAliveTime,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(builder.workerQueueSize),
                new ThreadFactoryBuilder().setNameFormat("ParallelBatchEvent-" + builder.namePrefix + "-pool-%d").build()
        );
        this.eventListener = builder.listener;
        this.eventTranslator = new HolderEventTranslator<>();

        //创建disruptor RingBuffer
        RingBuffer<Holder<T>> ringBuffer = RingBuffer.create(builder.producerType,
                new HolderEventFactory<>(),
                builder.bufferSize,
                builder.waitStrategy);

        // 创建屏障
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
        WorkHandler<Holder<T>>[] workHandlers = new WorkHandler[builder.threadPoolSize];
        for (int i = 0; i < workHandlers.length; i++) {
            workHandlers[i] = new HolderWorkHandler<>(builder.listener);
        }

        // 多消费者工作池
        WorkerPool<Holder<T>> workerPool = new WorkerPool<>(ringBuffer, sequenceBarrier, new HolderExceptionHandler<>(eventListener), workHandlers);
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
        this.workerPool = workerPool;
    }

    @Override
    public void add(T event) {
        final RingBuffer<Holder<T>> tempRingBuffer = ringBuffer;
        if (tempRingBuffer == null) {
            this.process(this.eventListener, new IllegalStateException("ParallelBatchEvent is closed"), event);
            return;
        }
        try {
            ringBuffer.publishEvent(this.eventTranslator, event);
        } catch (Exception e) {
            this.process(this.eventListener, new IllegalStateException("ParallelBatchEvent is closed"), event);
        }
    }

    @Override
    public void add(T... events) {
        final RingBuffer<Holder<T>> tempRingBuffer = ringBuffer;
        if (tempRingBuffer == null) {
            this.process(this.eventListener, new IllegalStateException("ParallelBatchEvent is closed"), events);
            return;
        }
        try {
            ringBuffer.publishEvents(this.eventTranslator, events);
        } catch (Exception e) {
            this.process(this.eventListener, new IllegalStateException("ParallelBatchEvent is closed"), events);
        }
    }

    @Override
    public boolean tryAdd(T event) {
        final RingBuffer<Holder<T>> tempRingBuffer = ringBuffer;
        if (tempRingBuffer == null) {
            return false;
        }
        try {
            return ringBuffer.tryPublishEvent(this.eventTranslator, event);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean tryAdd(T... events) {
        final RingBuffer<Holder<T>> tempRingBuffer = ringBuffer;
        if (tempRingBuffer == null) {
            return false;
        }
        try {
            return ringBuffer.tryPublishEvents(this.eventTranslator, events);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void start() {
        this.ringBuffer = workerPool.start(threadPoolExecutor);
    }

    @Override
    public boolean isShutdown() {
        return this.ringBuffer == null;
    }

    @Override
    public void shutdown() {
        RingBuffer<Holder<T>> tempRingBuffer = ringBuffer;
        ringBuffer = null;
        if (tempRingBuffer == null) {
            return;
        }
        if (workerPool != null) {
            workerPool.drainAndHalt();
        }
        if (threadPoolExecutor != null) {
            threadPoolExecutor.shutdown();
        }
    }

    private <T> void process(EventListener<T> listener, Throwable e, T event) {
        listener.onException(e, -1, event);
    }

    private <T> void process(EventListener<T> listener, Throwable e, T... events) {
        if (events == null) {
            return;
        }
        Arrays.stream(events).forEach(event -> process(listener, e, event));
    }

    public static class Builder<T> {

        private ProducerType producerType = ProducerType.MULTI;

        private int bufferSize = 16 * 1024;

        private int threadPoolSize = 1;

        private long keepAliveTime = 30;

        private int workerQueueSize = 4096;

        private String namePrefix = "";

        private WaitStrategy waitStrategy = new BlockingWaitStrategy();

        private EventListener<T> listener;

        public Builder<T> setProducerType(ProducerType producerType) {
            Preconditions.checkNotNull(producerType);
            this.producerType = producerType;
            return this;
        }

        public Builder<T> setThreadPoolSize(int threadPoolSize) {
            Preconditions.checkArgument(threadPoolSize > 0);
            this.threadPoolSize = threadPoolSize;
            return this;
        }

        public Builder<T> setKeepAliveTime(int keepAliveTime) {
            Preconditions.checkArgument(keepAliveTime > 0);
            this.keepAliveTime = keepAliveTime;
            return this;
        }

        public Builder<T> setWorkerQueueSize(int workerQueueSize) {
            Preconditions.checkArgument(workerQueueSize > 0);
            this.workerQueueSize = workerQueueSize;
            return this;
        }

        public Builder<T> setBufferSize(int bufferSize) {
            Preconditions.checkArgument(Integer.bitCount(bufferSize) == 1);
            this.bufferSize = bufferSize;
            return this;
        }

        public Builder<T> setNamePrefix(String namePrefix) {
            Preconditions.checkNotNull(namePrefix);
            this.namePrefix = namePrefix;
            return this;
        }

        public Builder<T> setWaitStrategy(WaitStrategy waitStrategy) {
            Preconditions.checkNotNull(waitStrategy);
            this.waitStrategy = waitStrategy;
            return this;
        }

        public Builder<T> setEventListener(EventListener<T> listener) {
            Preconditions.checkNotNull(listener);
            this.listener = listener;
            return this;
        }

        public ParallelBatchEvent<T> build() {
            return new ParallelBatchEvent<>(this);
        }
    }

}
