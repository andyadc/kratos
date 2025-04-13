package com.andyadc.kratos.filter.route.http;

import com.andyadc.kratos.common.constants.FilterConstants;
import com.andyadc.kratos.common.enums.FilterType;
import com.andyadc.kratos.common.enums.ResponseCode;
import com.andyadc.kratos.common.exception.ConnectionException;
import com.andyadc.kratos.common.exception.ResponseException;
import com.andyadc.kratos.common.time.SystemClock;
import com.andyadc.kratos.context.config.FilterConfig;
import com.andyadc.kratos.context.ctx.GatewayContext;
import com.andyadc.kratos.context.ctx.RequestResponseContext;
import com.andyadc.kratos.context.factory.ResponseFactory;
import com.andyadc.kratos.context.loader.GatewayConfigLoader;
import com.andyadc.kratos.exector.http.AsyncHttpHelper;
import com.andyadc.kratos.filter.api.annotation.Filter;
import com.andyadc.kratos.filter.api.entry.AbstractEntryGatewayFilter;
import com.andyadc.kratos.spi.annotation.SPIClass;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

@SPIClass
@Filter(
        id = FilterConstants.HTTP_FILTER_ID,
        name = FilterConstants.HTTP_FILTER_NAME,
        value = FilterType.ROUTE,
        order = FilterConstants.HTTP_FILTER_ORDER
)
public class HttpFilter extends AbstractEntryGatewayFilter<FilterConfig> {

    private final Logger logger = LoggerFactory.getLogger(HttpFilter.class);

    public HttpFilter() {
        super(FilterConfig.class);
    }

    @Override
    public void execute(GatewayContext gatewayContext, Object... args) throws Exception {
        RequestResponseContext requestResponseContext = (RequestResponseContext) gatewayContext;
        Request request = requestResponseContext.getModifyRequest().buildRequest();
        gatewayContext.setClientSendRequestTime(SystemClock.millisClock().now());

        CompletableFuture<Response> future = AsyncHttpHelper.getInstance().executeRequest(request);
        boolean whenComplete = GatewayConfigLoader.getGatewayConfig().isWhenComplete();
        // 单异步模式
        if (whenComplete) {
            future.whenComplete((response, throwable) -> {
                doComplete(request, response, throwable, requestResponseContext, args);
            });
        } else { // 双异步模式
            future.whenCompleteAsync((response, throwable) -> {
                doComplete(request, response, throwable, requestResponseContext, args);
            });
        }
    }

    /**
     * 真正执行的完成方法
     */
    private void doComplete(Request request, Response response, Throwable throwable, RequestResponseContext context, Object... args) {
        try {
            // 首先设置客户端接收响应的时间
            context.setClientReceiveResponseTime(SystemClock.millisClock().now());
            // 释放请求的资源
            context.releaseRequest();
            // 如果异常throwable不为空，说明已经出现了异常
            if (Objects.nonNull(throwable)) {
                String url = request.getUrl();
                // 访问超时
                if (throwable instanceof TimeoutException) {
                    logger.warn("http过滤器响应结果, 请求路径:{}, 耗时超过:{} ms", url, (request.getRequestTimeout() == null ?
                            GatewayConfigLoader.getGatewayConfig().getHttpRequestTimeout() : request.getRequestTimeout()));
                    context.setThrowable(new ResponseException(ResponseCode.REQUEST_TIMEOUT));
                } else {  // 其他异常
                    context.setThrowable(new ConnectionException(throwable, context.getUniqueId(), url, ResponseCode.HTTP_RESPONSE_ERROR));
                }
            } else {   //无异常
                context.setResponse(ResponseFactory.getGatewayResponse(response));
            }
        } catch (Throwable t) {
            // 兜底异常
            context.setThrowable(new ResponseException(ResponseCode.INTERNAL_ERROR));
            logger.error("http过滤器不获取到异常 ", t);
        } finally {
            try {
                context.written();
                super.nextFilter(context, args);
            } catch (Throwable t) {
                context.setThrowable(new ResponseException(ResponseCode.INTERNAL_ERROR));
                logger.error("nextFilter异常", t);
            }
        }
    }

}
