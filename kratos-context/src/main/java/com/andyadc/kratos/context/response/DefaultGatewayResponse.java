package com.andyadc.kratos.context.response;

import com.andyadc.kratos.common.constants.Constants;
import com.andyadc.kratos.common.enums.ResponseCode;
import com.andyadc.kratos.common.util.JsonUtils;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.asynchttpclient.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * 响应实现类
 */
public class DefaultGatewayResponse implements GatewayResponse {

    @Override
    public GatewayResponseData getGatewayResponse(Response response) {
        GatewayResponseData data = new GatewayResponseData();
        data.setFutureResponse(response);
        data.setHttpResponseStatus(HttpResponseStatus.valueOf(response.getStatusCode()));
        return data;
    }

    @Override
    public GatewayResponseData getGatewayResponse(ResponseCode code, Object... args) {
        GatewayResponseData data = new GatewayResponseData();
        data.setHttpResponseStatus(code.status());
        data.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8");

        Map<String, Object> map = new HashMap<>();
        map.put(Constants.STATUS, code.status().code());
        map.put(Constants.CODE, code.code());
        map.put(Constants.MESSAGE, code.message());
        data.setContent(JsonUtils.toJSONString(map));

        return data;
    }

    @Override
    public GatewayResponseData getGatewayResponse(Object content) {
        GatewayResponseData data = new GatewayResponseData();
        data.setHttpResponseStatus(ResponseCode.SUCCESS.status());
        data.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8");

        Map<String, Object> map = new HashMap<>();
        map.put(Constants.STATUS, ResponseCode.SUCCESS.status().code());
        map.put(Constants.CODE, ResponseCode.SUCCESS.code());
        map.put(Constants.MESSAGE, ResponseCode.SUCCESS.message());
        map.put(Constants.DATA, content);

        data.setContent(JsonUtils.toJSONString(map));
        return data;
    }
}
