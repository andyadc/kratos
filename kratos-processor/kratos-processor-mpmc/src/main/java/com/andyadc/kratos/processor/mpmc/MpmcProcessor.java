package com.andyadc.kratos.processor.mpmc;

import com.andyadc.kratos.common.enums.ResponseCode;
import com.andyadc.kratos.context.config.GatewayConfig;
import com.andyadc.kratos.context.factory.ResponseFactory;
import com.andyadc.kratos.context.request.HttpGatewayRequestWrapper;
import com.andyadc.kratos.processor.api.Processor;
import com.andyadc.kratos.processor.api.concurrent.queue.mpmc.queue.MpmcBlockingQueue;
import com.andyadc.kratos.spi.annotation.SPIClass;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Mpmc处理器
 */
@SPIClass
public class MpmcProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(MpmcProcessor.class);

    private GatewayConfig gatewayConfig;
    private Processor processor;
    private MpmcBlockingQueue<HttpGatewayRequestWrapper> mpmcBlockingQueue;
    private boolean usedExecutorPool;
    private ThreadPoolExecutor threadPoolExecutor;
    private volatile boolean isRunning = false;
    private Thread singleThreadExecutor;

    @Override
    public void init(GatewayConfig gatewayConfig, Processor processor) {
        this.gatewayConfig = gatewayConfig;
        this.processor = processor;
        this.mpmcBlockingQueue = new MpmcBlockingQueue<>(gatewayConfig.getBufferSize());
        this.usedExecutorPool = usedExecutorPool;
    }

    @Override
    public void process(HttpGatewayRequestWrapper httpRequestWrapper) throws Exception {
        this.mpmcBlockingQueue.put(httpRequestWrapper);
    }

    @Override
    public void start() {
        this.isRunning = true;
        this.processor.start();
        if (usedExecutorPool) {  //使用线程池
            this.threadPoolExecutor = new ThreadPoolExecutor(
                    gatewayConfig.getProcessThreads(),
                    gatewayConfig.getProcessThreads(),
                    gatewayConfig.getKeepAliveTime(),
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(gatewayConfig.getWorkerQueueSize())
            );
            for (int i = 0; i < gatewayConfig.getProcessThreads(); i++) {
                this.threadPoolExecutor.submit(new MpmcProcessorExecutor());
            }
        } else {   // 不使用线程池，开启单个线程执行
            this.singleThreadExecutor = new Thread(new MpmcProcessorExecutor());
            this.singleThreadExecutor.start();
        }
    }

    @Override
    public void shutdown() {
        this.isRunning = false;
        this.processor.shutdown();
        if (usedExecutorPool) {
            this.threadPoolExecutor.shutdown();
        }
    }

    private class MpmcProcessorExecutor implements Runnable {
        @Override
        public void run() {
            while (isRunning) {
                HttpGatewayRequestWrapper requestWrapper = null;
                try {
                    requestWrapper = mpmcBlockingQueue.take();
                    processor.process(requestWrapper);
                } catch (Throwable t) {
                    if (requestWrapper == null) {
                        logger.error("请求处理失败, request is null. {}", t.getMessage(), t);
                        return;
                    }
                    HttpRequest request = requestWrapper.getFullHttpRequest();
                    ChannelHandlerContext ctx = requestWrapper.getCtx();
                    try {
                        logger.error("请求处理失败, request:{}, message:{}", request, t.getMessage());
                        FullHttpResponse response = ResponseFactory.getHttpResponse(ResponseCode.INTERNAL_ERROR);
                        if (!HttpUtil.isKeepAlive(request)) {
                            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                        } else {
                            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                            ctx.writeAndFlush(response);
                        }
                    } catch (Exception e) {
                        logger.error("响应客户端数据失败, request:{}, message:{}", request, e.getMessage(), e);
                    }
                }
            }
        }
    }

}
