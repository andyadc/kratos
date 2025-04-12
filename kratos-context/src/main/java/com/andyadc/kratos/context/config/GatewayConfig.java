package com.andyadc.kratos.context.config;

import com.andyadc.kratos.common.constants.Constants;
import com.andyadc.kratos.common.enums.BufferType;
import com.andyadc.kratos.common.util.IPUtils;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;

/**
 * 网关通用配置类
 */
public class GatewayConfig {

    /**
     * 网关的默认端口
     */
    private int port = 10000;

    /**
     * 网关的唯一id
     */
    private String polarisId = IPUtils.getLocalIp() + Constants.COLON_SEPARATOR + port;

    /**
     * 注册中心地址
     */
    private String registryAddress = "";

    /**
     * 命名空间，取值为dev/test/prod
     */
    private String namespace = "dev";

    /**
     * 网关的环境，取值为dev/test/prod
     */
    private String env = "dev";

    /**
     * 网关CPU核心数
     */
    private int processThreads = Runtime.getRuntime().availableProcessors();

    /**
     * Netty的Boos线程数
     */
    private int eventLoopGroupBossThreads = 1;

    /**
     * Netty Worker线程数
     */
    private int eventLoopGroupWorkThreads = processThreads;

    /**
     * 是否开启EPoll
     */
    private boolean useEpoll = true;

    /**
     * 是否要开启Netty内存分配
     */
    private boolean nettyAllocator = true;

    /**
     * 请求body最大大小，128MB
     */
    private int maxContentLength = 128 * 1024 * 1024;

    /**
     * RPC连接数
     */
    private int rpcConnections = processThreads;

    /**
     * 响应模式，默认是单异步模式：CompletableFuture回调处理结果
     */
    private boolean whenComplete = true;

    /**
     * 网关缓冲模式，默认为MPMC模式
     */
    private String bufferType = BufferType.MPMC.getCode();

    /**
     * 网关内存队列大小
     */
    private int bufferSize = 16 * 1024;

    /**
     * 队列阻塞等待策略
     */
    private String waitStrategy = Constants.STRATEGY_BLOCKING;

    /**
     * 默认请求超时时间 3s
     */
    private long requestTimeout = 3000;

    /**
     * 默认路由转发的慢调用时间 2s
     */
    private long routeTimeout = 2000;

    /**
     * 连接超时时间
     */
    private int httpConnectTimeout = 30 * 1000;

    /**
     * 请求超时时间
     */
    private int httpRequestTimeout = 30 * 1000;

    /**
     * 客户端请求重试次数
     */
    private int httpMaxRequestRetry = 2;

    /**
     * 客户端请求最大连接数
     */
    private int httpMaxConnections = 10000;

    /**
     * 客户端每个地址支持的最大连接数
     */
    private int httpConnectionsPerHost = 8000;

    /**
     * 客户端空闲连接超时时间, 默认60秒
     */
    private int httpPooledConnectionIdleTimeout = 60 * 1000;

    /**
     * 默认指标发送Kafka底子好
     */
    private String kafkaAddress = "";

    /**
     * 内置指标发送Topic
     */
    private String metricTopic = "kratos-metric-topic";

    /**
     * 根据类型获取实际的等待策略
     */
    public WaitStrategy getWaitStrategy() {
        return switch (waitStrategy) {
            case Constants.STRATEGY_BLOCKING -> new BlockingWaitStrategy();
            case Constants.STRATEGY_BUSYSPIN -> new BusySpinWaitStrategy();
            case Constants.STRATEGY_YIELDING -> new YieldingWaitStrategy();
            case Constants.STRATEGY_SLEEPING -> new SleepingWaitStrategy();
            default -> new BlockingWaitStrategy();
        };
    }

    public void setWaitStrategy(String waitStrategy) {
        this.waitStrategy = waitStrategy;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPolarisId() {
        return polarisId;
    }

    public void setPolarisId(String polarisId) {
        this.polarisId = polarisId;
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public int getProcessThreads() {
        return processThreads;
    }

    public void setProcessThreads(int processThreads) {
        this.processThreads = processThreads;
    }

    public int getEventLoopGroupBossThreads() {
        return eventLoopGroupBossThreads;
    }

    public void setEventLoopGroupBossThreads(int eventLoopGroupBossThreads) {
        this.eventLoopGroupBossThreads = eventLoopGroupBossThreads;
    }

    public int getEventLoopGroupWorkThreads() {
        return eventLoopGroupWorkThreads;
    }

    public void setEventLoopGroupWorkThreads(int eventLoopGroupWorkThreads) {
        this.eventLoopGroupWorkThreads = eventLoopGroupWorkThreads;
    }

    public boolean isUseEpoll() {
        return useEpoll;
    }

    public void setUseEpoll(boolean useEpoll) {
        this.useEpoll = useEpoll;
    }

    public boolean isNettyAllocator() {
        return nettyAllocator;
    }

    public void setNettyAllocator(boolean nettyAllocator) {
        this.nettyAllocator = nettyAllocator;
    }

    public int getMaxContentLength() {
        return maxContentLength;
    }

    public void setMaxContentLength(int maxContentLength) {
        this.maxContentLength = maxContentLength;
    }

    public int getRpcConnections() {
        return rpcConnections;
    }

    public void setRpcConnections(int rpcConnections) {
        this.rpcConnections = rpcConnections;
    }

    public boolean isWhenComplete() {
        return whenComplete;
    }

    public void setWhenComplete(boolean whenComplete) {
        this.whenComplete = whenComplete;
    }

    public String getBufferType() {
        return bufferType;
    }

    public void setBufferType(String bufferType) {
        this.bufferType = bufferType;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public long getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(long requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public long getRouteTimeout() {
        return routeTimeout;
    }

    public void setRouteTimeout(long routeTimeout) {
        this.routeTimeout = routeTimeout;
    }

    public int getHttpConnectTimeout() {
        return httpConnectTimeout;
    }

    public void setHttpConnectTimeout(int httpConnectTimeout) {
        this.httpConnectTimeout = httpConnectTimeout;
    }

    public int getHttpRequestTimeout() {
        return httpRequestTimeout;
    }

    public void setHttpRequestTimeout(int httpRequestTimeout) {
        this.httpRequestTimeout = httpRequestTimeout;
    }

    public int getHttpMaxRequestRetry() {
        return httpMaxRequestRetry;
    }

    public void setHttpMaxRequestRetry(int httpMaxRequestRetry) {
        this.httpMaxRequestRetry = httpMaxRequestRetry;
    }

    public int getHttpMaxConnections() {
        return httpMaxConnections;
    }

    public void setHttpMaxConnections(int httpMaxConnections) {
        this.httpMaxConnections = httpMaxConnections;
    }

    public int getHttpConnectionsPerHost() {
        return httpConnectionsPerHost;
    }

    public void setHttpConnectionsPerHost(int httpConnectionsPerHost) {
        this.httpConnectionsPerHost = httpConnectionsPerHost;
    }

    public int getHttpPooledConnectionIdleTimeout() {
        return httpPooledConnectionIdleTimeout;
    }

    public void setHttpPooledConnectionIdleTimeout(int httpPooledConnectionIdleTimeout) {
        this.httpPooledConnectionIdleTimeout = httpPooledConnectionIdleTimeout;
    }

    public String getKafkaAddress() {
        return kafkaAddress;
    }

    public void setKafkaAddress(String kafkaAddress) {
        this.kafkaAddress = kafkaAddress;
    }

    public String getMetricTopic() {
        return metricTopic;
    }

    public void setMetricTopic(String metricTopic) {
        this.metricTopic = metricTopic;
    }

}
