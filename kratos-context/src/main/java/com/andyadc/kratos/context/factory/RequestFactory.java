package com.andyadc.kratos.context.factory;

import com.andyadc.kratos.common.constants.Constants;
import com.andyadc.kratos.common.constants.Protocol;
import com.andyadc.kratos.common.enums.ResponseCode;
import com.andyadc.kratos.common.exception.NullException;
import com.andyadc.kratos.common.exception.UnmatchPathException;
import com.andyadc.kratos.common.util.MatchUtils;
import com.andyadc.kratos.common.util.StringUtils;
import com.andyadc.kratos.context.attribute.AttributeKeyFactory;
import com.andyadc.kratos.context.cache.config.ConfigCache;
import com.andyadc.kratos.context.cache.config.ConfigCacheFactory;
import com.andyadc.kratos.context.ctx.GatewayContext;
import com.andyadc.kratos.context.ctx.RequestResponseContext;
import com.andyadc.kratos.context.definition.ServiceDefinition;
import com.andyadc.kratos.context.invoker.ServiceInvoker;
import com.andyadc.kratos.context.request.DefaultHttpGatewayRequest;
import com.andyadc.kratos.context.request.HttpGatewayRequest;
import com.andyadc.kratos.context.rule.Rule;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 获取请求信息工厂类
 */
public class RequestFactory {

    private static final Logger logger = LoggerFactory.getLogger(RequestFactory.class);

    /**
     * 获取网关上下文
     */
    public static GatewayContext getContext(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {
        // 获取请求request对象数据
        HttpGatewayRequest request = getRequest(fullHttpRequest, ctx);
        // 获取服务定义信息
        ServiceDefinition definition = getServiceDefinition(request);
        // 如果路径匹配失败，则直接抛出异常
        if (!MatchUtils.isMatch(definition.getPatternPath(), request.getPath())) {
            logger.warn("definition patternPath: {}, request path: {}", definition.getPatternPath(), request.getPath());
            throw new UnmatchPathException();
        }

        ServiceInvoker serviceInvoker = getServiceInoker(request, definition);
        String ruleId = serviceInvoker.getRuleId();
        ConfigCache cache = ConfigCacheFactory.getInstance();
        Rule rule = cache.getRule(ruleId);
        GatewayContext context = RequestResponseContext.getRequestResponseContext(definition.getProtocol(), ctx, HttpUtil.isKeepAlive(fullHttpRequest), request, rule);
        context.setServerReceiveRequestTime(request.getBeginTime());
        putContext(context, serviceInvoker);
        return context;
    }

    /**
     * 获取网关上下文
     */
    private static HttpGatewayRequest getRequest(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {
        HttpHeaders headers = fullHttpRequest.headers();
        String uniqueId = headers.get(Constants.UNIQUE_ID);
        if (StringUtils.isEmpty(uniqueId)) {
            logger.warn("uniqueId from request header is null.");
            throw new NullException(ResponseCode.REQUEST_PARSE_ERROR_NO_UNIQUEID);
        }
        String host = headers.get(HttpHeaderNames.HOST);
        HttpMethod method = fullHttpRequest.method();
        String uri = fullHttpRequest.uri();
        String clientIp = getClientIp(ctx, fullHttpRequest);
        String contentType = HttpUtil.getMimeType(fullHttpRequest) == null ? null : HttpUtil.getMimeType(fullHttpRequest).toString();
        Charset charset = HttpUtil.getCharset(fullHttpRequest, StandardCharsets.UTF_8);
        return new DefaultHttpGatewayRequest(uniqueId, charset, clientIp, host, uri, method, contentType, headers, fullHttpRequest);
    }

    /**
     * 获取客户端的ip地址
     */
    private static String getClientIp(ChannelHandlerContext ctx, FullHttpRequest request) {
        String forwardedValue = request.headers().get(Constants.HTTP_FORWARD_SEPARATOR);

        String clientIp = null;
        if (StringUtils.isNotEmpty(forwardedValue)) {
            List<String> values = Arrays.asList(forwardedValue.split(Constants.FORWARD_VALUE_SPLIT));
            if (!values.isEmpty() && StringUtils.isNotBlank(values.get(0))) {
                clientIp = values.get(0);
            }
        }
        if (clientIp == null) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            clientIp = inetSocketAddress.getAddress().getHostAddress();
        }
        return clientIp;
    }

    /**
     * 获取服务定义信息
     */
    public static ServiceDefinition getServiceDefinition(HttpGatewayRequest request) {
        ServiceDefinition definition = ConfigCacheFactory.getInstance().getServiceDefinition(request.getUniqueId());
        if (definition == null) {
            logger.warn("ServiceDefinition is null. UniqueId: {}", request.getUniqueId());
            throw new NullException(ResponseCode.SERVICE_DEFINITION_NOT_FOUND);
        }
        return definition;
    }

    /**
     * 根据上下文和服务定义信息获取ServiceInvoker
     */
    private static ServiceInvoker getServiceInoker(HttpGatewayRequest context, ServiceDefinition definition) {
        Map<String, ServiceInvoker> invokerMap = definition.getInvokerMap();
        ServiceInvoker serviceInvoker = invokerMap.get(context.getPath());
        if (serviceInvoker == null) {
            logger.warn("ServiceInvoker is null. Path: {}", context.getPath());
            throw new NullException(ResponseCode.SERVICE_INVOKER_NOT_FOUND);
        }
        return serviceInvoker;
    }

    /**
     * 将上下文信息设置到Context
     */
    private static void putContext(GatewayContext context, ServiceInvoker invoker) {
        switch (context.getProtocol()) {
            case Protocol.HTTP -> context.putAttribute(AttributeKeyFactory.getHttpInvoker(), invoker);
            case Protocol.RPC -> context.putAttribute(AttributeKeyFactory.getRpcInvoker(), invoker);
            case Protocol.WEBSOCKET -> context.putAttribute(AttributeKeyFactory.getWebSocketInvoker(), invoker);
            default -> context.putAttribute(AttributeKeyFactory.getHttpInvoker(), invoker);
        }
    }

}
