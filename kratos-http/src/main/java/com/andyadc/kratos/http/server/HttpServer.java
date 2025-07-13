package com.andyadc.kratos.http.server;

import com.andyadc.kratos.common.constants.Constants;
import com.andyadc.kratos.common.exception.GatewayException;
import com.andyadc.kratos.common.util.RemotingUtils;
import com.andyadc.kratos.context.config.GatewayConfig;
import com.andyadc.kratos.http.processor.HttpProcessor;
import com.andyadc.kratos.processor.api.Processor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class HttpServer implements HttpProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);
    private final GatewayConfig gatewayConfig;
    private int port = 10000;
    private ServerBootstrap serverBootstrap;
    private EventLoopGroup eventBossGroup;
    private EventLoopGroup eventWorkGroup;
    private final Processor processor;

    public HttpServer(GatewayConfig gatewayConfig, Processor processor) {
        this.gatewayConfig = gatewayConfig;
        this.processor = processor;
        if (gatewayConfig.getPort() > Constants.MIN_PORT && gatewayConfig.getPort() < Constants.MAX_PORT) {
            this.port = gatewayConfig.getPort();
        }
        // 执行初始化方法
        this.init();
    }

    @Override
    public void init() {
        this.serverBootstrap = new ServerBootstrap();
        if (useEPoll()) {
            this.eventBossGroup = new EpollEventLoopGroup(gatewayConfig.getEventLoopGroupBossThreads(),
                    new DefaultThreadFactory(Constants.BOSS_EPOLL));
            this.eventWorkGroup = new EpollEventLoopGroup(gatewayConfig.getEventLoopGroupWorkThreads(),
                    new DefaultThreadFactory(Constants.WORK_EPOLL));
        } else {
            this.eventBossGroup = new NioEventLoopGroup(gatewayConfig.getEventLoopGroupBossThreads(),
                    new DefaultThreadFactory(Constants.BOSS_EPOLL));
            this.eventWorkGroup = new NioEventLoopGroup(gatewayConfig.getEventLoopGroupWorkThreads(),
                    new DefaultThreadFactory(Constants.WORK_EPOLL));
        }
    }


    @Override
    public void start() {
        ServerBootstrap handler = this.serverBootstrap
                .group(eventBossGroup, eventWorkGroup)
                .channel(useEPoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_SNDBUF, 65535)
                .childOption(ChannelOption.SO_RCVBUF, 65535)
                .localAddress(new InetSocketAddress(this.port))
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(
                                new HttpServerCodec(),
                                new HttpObjectAggregator(gatewayConfig.getMaxContentLength()),
                                new HttpServerExpectContinueHandler(),
                                new HttpServerConnectionHandler(),
                                new HttpServerHandler(processor)
                        );
                    }
                });

        if (gatewayConfig.isNettyAllocator()) {
            handler.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        }
        try {
            this.serverBootstrap.bind().sync();
            logger.info("Polaris网关启动成功，监听端口:{}", this.port);
        } catch (Exception e) {
            throw new GatewayException("Polaris网关启动失败", e);
        }
    }

    @Override
    public void shutdown() {
        if (eventBossGroup != null) {
            eventBossGroup.shutdownGracefully();
        }
        if (eventWorkGroup != null) {
            eventWorkGroup.shutdownGracefully();
        }
    }

    private boolean useEPoll() {
        return gatewayConfig.isUseEpoll() && RemotingUtils.isLinuxPlatform() && Epoll.isAvailable();
    }

    public EventLoopGroup getEventLoopGroupWork() {
        return eventWorkGroup;
    }

}
