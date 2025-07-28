package com.andyadc.kratos.container.client;

import com.andyadc.kratos.container.Container;
import com.andyadc.kratos.context.config.GatewayConfig;
import com.andyadc.kratos.exector.http.AsyncHttpExecutor;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.EventLoopGroup;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * 客户端实现
 */
public class HttpClientContainer implements Container {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientContainer.class);
    private final GatewayConfig gatewayConfig;
    private final EventLoopGroup eventWorkGroup;
    private AsyncHttpClient asyncHttpClient;
    private DefaultAsyncHttpClientConfig.Builder builder;

    public HttpClientContainer(GatewayConfig gatewayConfig, EventLoopGroup eventWorkGroup) {
        this.gatewayConfig = gatewayConfig;
        this.eventWorkGroup = eventWorkGroup;
        // 调用初始化方法
        this.init();
    }

    @Override
    public void init() {
        this.builder = new DefaultAsyncHttpClientConfig.Builder()
                .setFollowRedirect(false)
                .setEventLoopGroup(eventWorkGroup)
                .setConnectTimeout(Duration.ofMillis(gatewayConfig.getHttpConnectTimeout()))
                .setRequestTimeout(Duration.ofMillis(gatewayConfig.getHttpRequestTimeout()))
                .setMaxRequestRetry(gatewayConfig.getHttpMaxRequestRetry())
                .setAllocator(PooledByteBufAllocator.DEFAULT)
                .setCompressionEnforced(true)
                .setMaxConnections(gatewayConfig.getHttpMaxConnections())
                .setMaxConnectionsPerHost(gatewayConfig.getHttpConnectionsPerHost())
                .setPooledConnectionIdleTimeout(Duration.ofMillis(gatewayConfig.getHttpPooledConnectionIdleTimeout()));
    }

    @Override
    public void start() {
        this.asyncHttpClient = new DefaultAsyncHttpClient(builder.build());
        AsyncHttpExecutor.getInstance().initialized(this.asyncHttpClient);
    }

    @Override
    public void shutdown() {
        if (this.asyncHttpClient != null) {
            try {
                this.asyncHttpClient.close();
            } catch (Exception e) {
                logger.error("HttpClientContainer shutdown error.", e);
            }
        }
    }

}
