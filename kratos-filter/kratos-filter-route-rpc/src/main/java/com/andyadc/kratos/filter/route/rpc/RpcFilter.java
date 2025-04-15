package com.andyadc.kratos.filter.route.rpc;

import com.andyadc.kratos.common.constants.FilterConstants;
import com.andyadc.kratos.common.enums.FilterType;
import com.andyadc.kratos.common.enums.ResponseCode;
import com.andyadc.kratos.common.exception.ResponseException;
import com.andyadc.kratos.common.exception.RpcConnectionException;
import com.andyadc.kratos.common.time.SystemClock;
import com.andyadc.kratos.common.util.JsonUtils;
import com.andyadc.kratos.context.attribute.AttributeKeyFactory;
import com.andyadc.kratos.context.config.FilterConfig;
import com.andyadc.kratos.context.ctx.GatewayContext;
import com.andyadc.kratos.context.ctx.RequestResponseContext;
import com.andyadc.kratos.context.factory.ResponseFactory;
import com.andyadc.kratos.context.invoker.RpcServiceInvoker;
import com.andyadc.kratos.context.invoker.ServiceInvoker;
import com.andyadc.kratos.context.loader.GatewayConfigLoader;
import com.andyadc.kratos.context.request.RpcGatewayRequest;
import com.andyadc.kratos.context.response.GatewayResponseData;
import com.andyadc.kratos.exector.rpc.RpcHelper;
import com.andyadc.kratos.filter.api.annotation.Filter;
import com.andyadc.kratos.filter.api.entry.AbstractEntryGatewayFilter;
import com.andyadc.kratos.spi.annotation.SPIClass;
import io.netty.handler.codec.http.HttpHeaderValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@SPIClass
@Filter(
        id = FilterConstants.RPC_FILTER_ID,
        name = FilterConstants.HTTP_FILTER_NAME,
        value = FilterType.ROUTE,
        order = FilterConstants.RPC_FILTER_ORDER
)
public class RpcFilter extends AbstractEntryGatewayFilter<FilterConfig> {

    private final Logger logger = LoggerFactory.getLogger(RpcFilter.class);

    public RpcFilter() {
        super(FilterConfig.class);
    }

    @Override
    public void execute(GatewayContext gatewayContext, Object... args) throws Exception {
        RequestResponseContext requestResponseContext = (RequestResponseContext) gatewayContext;
        ServiceInvoker serviceInvoker = requestResponseContext.getRequiredAttribute(AttributeKeyFactory.getRpcInvoker());
        RpcServiceInvoker rpcServiceInvoker = (RpcServiceInvoker) serviceInvoker;
        // 检验请求协议
        if (!HttpHeaderValues.APPLICATION_JSON.toString().equals(requestResponseContext.getOriginRequest().getContentType())) {
            // 抛出异常，终止执行
            requestResponseContext.terminated();
            throw new ResponseException(ResponseCode.RPC_PARAMETER_VALUE_ERROR);
        }
        String body = requestResponseContext.getOriginRequest().getBody();
        // 释放请求对象
        requestResponseContext.releaseRequest();
        List<Object> parameters = null;
        try {
            parameters = JsonUtils.parseToList(body, Object.class);
//            parameters = JsonConvertUtils.convertJSONToArray(body, Object.class);
        } catch (Exception e) {
            // 解析异常，则终止执行，并抛出异常
            requestResponseContext.terminated();
            throw new ResponseException(ResponseCode.RPC_PARAMETER_VALUE_ERROR);
        }
        //构建请求对象
        RpcGatewayRequest rpcPolarisRequest = RpcHelper.buildRpcRequest(rpcServiceInvoker, parameters.toArray());
        requestResponseContext.setClientSendRequestTime(SystemClock.millisClock().now());
        CompletableFuture<Object> future = RpcHelper.getInstance().$invokeAsync(requestResponseContext, rpcPolarisRequest);

        // 异步模式
        boolean whenComplete = GatewayConfigLoader.getGatewayConfig().isWhenComplete();

        // 单异步模式
        if (whenComplete) {
            future.whenComplete((response, throwable) -> {
                doComplete(rpcServiceInvoker, response, throwable, requestResponseContext, args);
            });
        } else {  // 双异步模式
            future.whenCompleteAsync((response, throwable) -> {
                doComplete(rpcServiceInvoker, response, throwable, requestResponseContext, args);
            });
        }
    }

    private void doComplete(RpcServiceInvoker rpcServiceInvoker, Object response, Throwable throwable, RequestResponseContext context, Object... args) {
        try {
            context.setClientReceiveResponseTime(SystemClock.millisClock().now());
            // 如果异常不为空，说明在链路上已经产生了异常
            if (Objects.nonNull(throwable)) {
                RpcConnectionException rpcConnectionException = new RpcConnectionException(throwable,
                        context.getUniqueId(),
                        context.getOriginRequest().getPath(),
                        rpcServiceInvoker.getInterfaceClass(),
                        rpcServiceInvoker.getMethodName(),
                        ResponseCode.RPC_RESPONSE_ERROR);
                context.setThrowable(rpcConnectionException);
            } else {
                GatewayResponseData rpcResponse = ResponseFactory.getGatewayResponse(response);
                context.setResponse(rpcResponse);
            }
        } catch (Throwable t) {
            // 兜底的异常处理
            context.setThrowable(new ResponseException(ResponseCode.INTERNAL_ERROR));
            logger.error("unknown exception, ", t);
        } finally {
            try {
                context.written();
                super.nextFilter(context, args);
            } catch (Throwable t) {
                context.setThrowable(new ResponseException(ResponseCode.INTERNAL_ERROR));
                logger.error("execute nextFilter exception ", t);
            }
        }
    }

}
