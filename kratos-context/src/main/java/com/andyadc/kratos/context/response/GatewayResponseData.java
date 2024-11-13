package com.andyadc.kratos.context.response;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.asynchttpclient.Response;

import java.io.Serializable;

/**
 * 返回的数据
 */
public class GatewayResponseData implements Serializable {

    private static final long serialVersionUID = 7234071907494922073L;

    /**
     * 扩展的响应头
     */
    private final HttpHeaders extraResponseHeaders = new DefaultHttpHeaders();
    /**
     * 响应头
     */
    private HttpHeaders responseHeaders = new DefaultHttpHeaders();
    /**
     * 响应内容
     */
    private String content;

    /**
     * 状态码
     */
    private HttpResponseStatus httpResponseStatus;

    /**
     * 响应对象
     */
    private Response futureResponse;

    public GatewayResponseData() {
    }

    public GatewayResponseData(HttpHeaders responseHeaders, String content, HttpResponseStatus httpResponseStatus, Response futureResponse) {
        this.responseHeaders = responseHeaders;
        this.content = content;
        this.httpResponseStatus = httpResponseStatus;
        this.futureResponse = futureResponse;
    }

    public void putHeader(CharSequence key, CharSequence val) {
        responseHeaders.add(key, val);
    }

    public HttpHeaders getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(HttpHeaders responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public HttpHeaders getExtraResponseHeaders() {
        return extraResponseHeaders;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public HttpResponseStatus getHttpResponseStatus() {
        return httpResponseStatus;
    }

    public void setHttpResponseStatus(HttpResponseStatus httpResponseStatus) {
        this.httpResponseStatus = httpResponseStatus;
    }

    public Response getFutureResponse() {
        return futureResponse;
    }

    public void setFutureResponse(Response futureResponse) {
        this.futureResponse = futureResponse;
    }
}
