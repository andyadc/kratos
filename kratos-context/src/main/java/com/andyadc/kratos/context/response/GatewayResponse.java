package com.andyadc.kratos.context.response;

import com.andyadc.kratos.common.enums.ResponseCode;
import org.asynchttpclient.Response;

public interface GatewayResponse {

    /**
     * 获取网关的响应对象
     */
    GatewayResponseData getGatewayResponse(Response response);

    /**
     * 获取网关的响应对象
     */
    GatewayResponseData getGatewayResponse(ResponseCode code, Object... args);

    /**
     * 获取网关响应对象
     */
    GatewayResponseData getGatewayResponse(Object content);
}
