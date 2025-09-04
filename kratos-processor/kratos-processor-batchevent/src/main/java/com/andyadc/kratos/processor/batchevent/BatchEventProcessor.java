package com.andyadc.kratos.processor.batchevent;

import com.andyadc.kratos.context.config.GatewayConfig;
import com.andyadc.kratos.context.request.HttpGatewayRequestWrapper;
import com.andyadc.kratos.processor.api.Processor;
import com.andyadc.kratos.processor.api.concurrent.queue.batchevent.event.BatchEvent;
import com.andyadc.kratos.processor.api.concurrent.queue.batchevent.event.ParallelBatchEvent;
import com.andyadc.kratos.processor.api.concurrent.queue.batchevent.listener.BatchEventListener;
import com.andyadc.kratos.spi.annotation.SPIClass;
import com.lmax.disruptor.dsl.ProducerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BatchEvent缓冲区队列的核心实现，最终还是会通过NettyCoreProcessor执行
 */
@SPIClass
public class BatchEventProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(BatchEventProcessor.class);

    private static final String THREAD_PREFIX = "batch-event-";
    private Processor processor;
    private BatchEvent<HttpGatewayRequestWrapper> batchEvent;

    @Override
    public void init(GatewayConfig gatewayConfig, Processor processor) {
        this.processor = processor;
        ParallelBatchEvent.Builder<HttpGatewayRequestWrapper> builder = new ParallelBatchEvent.Builder<HttpGatewayRequestWrapper>()
                .setBufferSize(gatewayConfig.getBufferSize())
                .setThreadPoolSize(gatewayConfig.getProcessThreads())
                .setProducerType(ProducerType.MULTI)
                .setNamePrefix(THREAD_PREFIX)
                .setWaitStrategy(gatewayConfig.getWaitStrategy());

        BatchEventListener batchEventListener = new BatchEventListener(processor);
        builder.setEventListener(batchEventListener);
        this.batchEvent = builder.build();
    }

    @Override
    public void process(HttpGatewayRequestWrapper httpRequestWrapper) throws Exception {
        this.batchEvent.add(httpRequestWrapper);
    }

    @Override
    public void start() {
        this.processor.start();
        this.batchEvent.start();
        logger.info("BatchEventProcessor startup.");
    }

    @Override
    public void shutdown() {
        this.processor.shutdown();
        this.batchEvent.shutdown();
        logger.info("BatchEventProcessor shutdown completed.");
    }

}
