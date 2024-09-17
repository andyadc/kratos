package com.andyadc.kratos.context.request;

import com.andyadc.kratos.common.constants.Constants;
import com.andyadc.kratos.common.time.SystemClock;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import org.asynchttpclient.RequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DefaultHttpGatewayRequest {

    private final Logger logger = LoggerFactory.getLogger(DefaultHttpGatewayRequest.class);

    /**
     * 服务的唯一id，封装FullHttpRequest请求时在Header中需要有此值
     * 此值的构成就是serviceId:version
     */
    private final String uniqueId;
    /**
     * 请求开始时间
     */
    private final long beginTime;
    /**
     * 请求编码
     */
    private final Charset charset;
    /**
     * 客户端的ip地址，后续可以针对客户端进行流控、限流、以及黑白名单等
     */
    private final String clientIp;
    /**
     * 请求的目标地址，ip:port
     */
    private final String targetHost;

    /**
     * 请求的路径，带有参数，例如/xxx/yyy/zzz?aaa=bbb&ccc=ddd
     */
    private final String uri;

    /**
     * 请求的路径，不带参数，例如，/xxx/yyy/zzz
     */
    private final String path;

    /**
     * 请求方式，包含：GET/POST/PUT/DELETE等
     */
    private final HttpMethod method;
    /**
     * 请求格式，例如Content-Type:application/json
     */
    private final String contentType;
    /**
     * 请求头
     */
    private final HttpHeaders headers;

    /**
     * 主要用来解析参数
     */
    private final QueryStringDecoder queryDecoder;

    /**
     * 最终要发出http请求的对象
     */
    private final FullHttpRequest fullHttpRequest;
    /**
     * 主要用于构建Http请求对象
     */
    private final RequestBuilder requestBuilder;
    /**
     * 请求协议，默认是http://
     */
    private final String requestSchema;
    /**
     * 请求host
     */
    private final String requestHost;
    /**
     * 请求path
     */
    private final String requestPath;
    /**
     * 请求体数据
     */
    private String body;
    /**
     * 存放cookie信息
     */
    private Map<String, Cookie> cookieMap;
    /**
     * 定义的参数集合
     */
    private Map<String, List<String>> postParams;

    public DefaultHttpGatewayRequest(String uniqueId, Charset charset, String clientIp, String targetHost, String uri, HttpMethod method,
                                     String contentType, HttpHeaders headers, FullHttpRequest fullHttpRequest) {
        this.uniqueId = uniqueId;
        this.charset = charset;
        this.clientIp = clientIp;
        this.targetHost = targetHost;
        this.uri = uri;
        this.method = method;
        this.contentType = contentType;
        this.headers = headers;
        this.fullHttpRequest = fullHttpRequest;
        this.beginTime = SystemClock.millisClock().now();
        this.queryDecoder = new QueryStringDecoder(uri, charset);
        this.path = queryDecoder.path();
        this.requestHost = targetHost;
        this.requestPath = path;
        this.requestSchema = Constants.HTTP_PREFIX_SEPARATOR;
        this.requestBuilder = new RequestBuilder();
        this.requestBuilder.setMethod(method.name());
        this.requestBuilder.setHeaders(headers);
        this.requestBuilder.setQueryParams(queryDecoder.parameters());
        ByteBuf buf = fullHttpRequest.content();
        if (Objects.nonNull(buf)) {
            this.requestBuilder.setBody(buf.nioBuffer());
        }
    }

    public Cookie getCookie(String name) {
        if (cookieMap == null) {
            cookieMap = new HashMap<>();
            String cookieContent = this.getHeaders().get(HttpHeaderNames.COOKIE);
            Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieContent);
            for (Cookie cookie : cookies) {
                cookieMap.put(name, cookie);
            }
        }
        return cookieMap.get(name);
    }

    public List<String> getQueryParameters(String name) {
        return queryDecoder.parameters().get(name);
    }

    public List<String> getPostParameters(String name) {
        String body = getBody();
        if (isFormPost()) {
            if (postParams == null) {
                QueryStringDecoder paramDecoder = new QueryStringDecoder(body, false);
                postParams = paramDecoder.parameters();
            }

            if (postParams == null || postParams.isEmpty()) {
                return null;
            }
            return postParams.get(name);
        } else if (isJsonPost()) {
            try {
                return Lists.newArrayList(JsonPath.read(body, name).toString());
            } catch (Exception e) {
                //	ignore
                logger.error("解析参数失败，name: {}, body: {}", name, body, e);
            }
        }
        return null;
    }

    private boolean isFormPost() {
        return HttpMethod.POST.equals(method) &&
                (contentType.startsWith(HttpHeaderValues.FORM_DATA.toString()) ||
                        contentType.startsWith(HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString()));
    }

    private boolean isJsonPost() {
        return HttpMethod.POST.equals(method) &&
                contentType.startsWith(HttpHeaderValues.APPLICATION_JSON.toString());
    }
}
