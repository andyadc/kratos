package com.andyadc.kratos.context.config;

import com.andyadc.kratos.common.constants.Constants;
import com.andyadc.kratos.common.enums.BufferType;
import com.andyadc.kratos.common.enums.ProcessorType;
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
    private String gatewayId = IPUtils.getLocalIp() + Constants.COLON_SEPARATOR + port;

    /**
     * 网关的服务名称
     */
    private String gatewayName = "kratos";

    /**
     * 版本号
     */
    private String version = "1.0.0";

    /**
     * 权重，默认为100
     */
    private int weight = 100;

    /**
     * 是否是灰度发布
     */
    private boolean gray;

    /**
     * 注册中心地址
     */
    private String registryAddress = "";

    /**
     * 注册中心类型，取值为：nacosRegistry/zookeeperRegistry/etcdRegistry/consulRegistry
     * 通过SPI加载对应的注册中心实现类
     */
    private String registryType = "nacosRegistry";

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
     * 线程空闲存活时间
     */
    private long keepAliveTime = 30;

    /**
     * 队列大小
     */
    private int workerQueueSize = 4096;

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
     * 网关处理器模式，默认为MPMC_PROCESSOR
     */
    private String processorType = ProcessorType.MPMC_PROCESSOR.getCode();

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
     * 默认指标发送Kafka地址
     */
    private String kafkaAddress = "";

    /**
     * 内置指标发送Topic
     */
    private String metricTopic = "kratos-metric-topic";

    /**
     * 默认的配置文件名称
     */
    private String configFileName = "kratos.properties";

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

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isGray() {
        return gray;
    }

    public void setGray(boolean gray) {
        this.gray = gray;
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public String getRegistryType() {
        return registryType;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
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

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public int getWorkerQueueSize() {
        return workerQueueSize;
    }

    public void setWorkerQueueSize(int workerQueueSize) {
        this.workerQueueSize = workerQueueSize;
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

    public String getProcessorType() {
        return processorType;
    }

    public void setProcessorType(String processorType) {
        this.processorType = processorType;
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

    public String getConfigFileName() {
        return configFileName;
    }

    public void setConfigFileName(String configFileName) {
        this.configFileName = configFileName;
    }
}
