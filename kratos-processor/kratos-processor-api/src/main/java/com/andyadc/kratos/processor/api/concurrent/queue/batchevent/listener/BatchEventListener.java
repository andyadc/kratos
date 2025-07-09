package com.andyadc.kratos.processor.api.concurrent.queue.batchevent.listener;

import com.andyadc.kratos.common.enums.ResponseCode;
import com.andyadc.kratos.context.factory.ResponseFactory;
import com.andyadc.kratos.context.request.HttpGatewayRequestWrapper;
import com.andyadc.kratos.processor.api.Processor;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchEventListener implements EventListener<HttpGatewayRequestWrapper> {

    private static final Logger logger = LoggerFactory.getLogger(BatchEventListener.class);

    private final Processor processor;

    public BatchEventListener(Processor processor) {
        this.processor = processor;
    }

    @Override
    public void onEvent(HttpGatewayRequestWrapper event) throws Exception {
        this.processor.process(event);
    }

    @Override
    public void onException(Throwable ex, long sequence, HttpGatewayRequestWrapper event) {
        HttpRequest request = event.getFullHttpRequest();
        ChannelHandlerContext ctx = event.getCtx();
        try {
            logger.error("请求处理失败, request:{}, message:{}", request, ex.getMessage(), ex);
            // 构建异常响应对象
            FullHttpResponse fullHttpResponse = ResponseFactory.getHttpResponse(ResponseCode.INTERNAL_ERROR);
            if (!HttpUtil.isKeepAlive(request)) {
                ctx.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
            } else {
                fullHttpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                ctx.writeAndFlush(fullHttpResponse);
            }
        } catch (Exception e) {
            logger.error("异常信息回写客户端失败, request:{}, message:{}", request, e.getMessage(), e);
        }
    }

}
