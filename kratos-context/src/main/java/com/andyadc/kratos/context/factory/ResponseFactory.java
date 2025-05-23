package com.andyadc.kratos.context.factory;

import com.andyadc.kratos.common.constants.Constants;
import com.andyadc.kratos.common.enums.ResponseCode;
import com.andyadc.kratos.context.ctx.GatewayContext;
import com.andyadc.kratos.context.response.DefaultGatewayResponse;
import com.andyadc.kratos.context.response.GatewayResponse;
import com.andyadc.kratos.context.response.GatewayResponseData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import org.asynchttpclient.Response;

import java.util.Objects;

/**
 * 获取响应信息工厂类
 */
public class ResponseFactory {

    private static final GatewayResponse GATEWAY_RESPONSE = new DefaultGatewayResponse();

    public static GatewayResponseData getGatewayResponse(Response response) {
        return GATEWAY_RESPONSE.getGatewayResponse(response);
    }

    /**
     * 获取网关响应对象
     */
    public static GatewayResponseData getGatewayResponse(Object content) {
        return GATEWAY_RESPONSE.getGatewayResponse(content);
    }

    /**
     * 获取网关的响应对象
     */
    public static GatewayResponseData getGatewayResponseWithCode(ResponseCode code, Object... args) {
        return GATEWAY_RESPONSE.getGatewayResponse(code, args);
    }

    /**
     * 获取响应对象
     */
    public static FullHttpResponse getHttpResponse(ResponseCode responseCode) {
        GatewayResponseData response = GATEWAY_RESPONSE.getGatewayResponse(responseCode);
        DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.INTERNAL_SERVER_ERROR,
                Unpooled.wrappedBuffer(response.getContent().getBytes()));
        httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON.concat(";charset=utf-8"));
        httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
        return httpResponse;
    }

    /**
     * 获取FullHttpResponse对象
     */
    public static FullHttpResponse getHttpResponse(GatewayContext context, GatewayResponseData response) {
        ByteBuf content;

        if (Objects.nonNull(response.getFutureResponse())) {
            content = Unpooled.wrappedBuffer(response.getFutureResponse().getResponseBodyAsByteBuffer());
        } else if (response.getContent() != null) {
            content = Unpooled.wrappedBuffer(response.getContent().getBytes());
        } else {
            content = Unpooled.wrappedBuffer(Constants.BLANK_SEPARATOR_1.getBytes());
        }

        if (Objects.isNull(response.getFutureResponse())) {
            DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    response.getHttpResponseStatus(), content);
            httpResponse.headers().add(response.getResponseHeaders());
            httpResponse.headers().add(response.getExtraResponseHeaders());
            httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
            return httpResponse;
        } else {
            response.getFutureResponse().getHeaders().add(response.getExtraResponseHeaders());
            DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.valueOf(response.getFutureResponse().getStatusCode()),
                    content);
            httpResponse.headers().add(response.getFutureResponse().getHeaders());
            return httpResponse;
        }
    }

}
