package com.andyadc.kratos.container.server.handler;

import com.andyadc.kratos.context.request.HttpGatewayRequestWrapper;
import com.andyadc.kratos.processor.api.Processor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务端核心处理器
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

    private final Processor processor;

    public HttpServerHandler(Processor processor) {
        this.processor = processor;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            HttpGatewayRequestWrapper wrapper = new HttpGatewayRequestWrapper();
            wrapper.setFullHttpRequest(request);
            wrapper.setCtx(ctx);
            // 调用processor的process方法，执行处理器驱动过滤器链的执行
            processor.process(wrapper);
        } else {
            logger.error("The current request is not an HTTP request, msg:{}", msg);
            if (!ReferenceCountUtil.release(msg)) {
                logger.error("release error");
            }
        }
    }

}
