package com.andyadc.kratos.processor.batchevent;

import com.andyadc.kratos.context.request.HttpGatewayRequestWrapper;
import com.andyadc.kratos.processor.api.Processor;
import com.andyadc.kratos.spi.annotation.SPIClass;

/**
 * BatchEvent缓冲区队列的核心实现，最终还是会通过NettyCoreProcessor执行
 */
@SPIClass
public class BatchEventProcessor implements Processor {

    @Override
    public void process(HttpGatewayRequestWrapper httpRequestWrapper) throws Exception {

    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }

}
