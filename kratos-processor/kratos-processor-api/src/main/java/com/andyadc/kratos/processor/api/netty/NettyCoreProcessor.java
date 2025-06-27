package com.andyadc.kratos.processor.api.netty;

import com.andyadc.kratos.common.enums.ResponseCode;
import com.andyadc.kratos.common.exception.NoMatchPathException;
import com.andyadc.kratos.common.exception.NotFoundException;
import com.andyadc.kratos.common.exception.ResponseException;
import com.andyadc.kratos.context.factory.RequestFactory;
import com.andyadc.kratos.context.factory.ResponseFactory;
import com.andyadc.kratos.context.request.HttpGatewayRequestWrapper;
import com.andyadc.kratos.filter.api.factory.GatewayFilterFactory;
import com.andyadc.kratos.filter.api.factory.SingletonFilterFactory;
import com.andyadc.kratos.processor.api.Processor;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 整体执行的核心流程
 */
public class NettyCoreProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(NettyCoreProcessor.class);

    /**
     * 过滤器的工厂实现对象
     */
    private final GatewayFilterFactory gatewayFilterFactory = SingletonFilterFactory.getInstance();

    @Override
    public void process(HttpGatewayRequestWrapper httpRequestWrapper) throws Exception {
        FullHttpRequest request = httpRequestWrapper.getFullHttpRequest();
        ChannelHandlerContext ctx = httpRequestWrapper.getCtx();
        try {
            // 执行过滤器链
            gatewayFilterFactory.executeFilterChain(RequestFactory.getContext(request, ctx));
        } catch (NoMatchPathException e) {
            logger.error("路径匹配失败, code:{}, msg:{}", e.getResponseCode().code(), e.getResponseCode().message(), e);
            FullHttpResponse response = ResponseFactory.getHttpResponse(e.getResponseCode());
            this.writeAndRelease(ctx, request, response);
        } catch (NotFoundException e) {
            logger.error("未找到网关资源, code:{}, msg:{}", e.getResponseCode().code(), e.getResponseCode().message(), e);
            FullHttpResponse response = ResponseFactory.getHttpResponse(e.getResponseCode());
            this.writeAndRelease(ctx, request, response);
        } catch (ResponseException e) {
            logger.error("网关响应数据异常, code:{}, msg:{}", e.getResponseCode().code(), e.getResponseCode().message(), e);
            FullHttpResponse response = ResponseFactory.getHttpResponse(e.getResponseCode());
            this.writeAndRelease(ctx, request, response);
        } catch (Throwable t) {
            logger.error("网关内部异常,", t);
            FullHttpResponse response = ResponseFactory.getHttpResponse(ResponseCode.INTERNAL_ERROR);
            this.writeAndRelease(ctx, request, response);
        }
    }

    /**
     * 向调用方写响应信息，并释放资源
     */
    private void writeAndRelease(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response) {
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        if (!ReferenceCountUtil.release(request)) {
            logger.warn("释放资源失败, uri:{}", request.uri());
        }
    }

    @Override
    public void start() {
        // 空方法，交由子类实现
    }

    @Override
    public void shutdown() {
        // 空方法，交由子类实现
    }

}
