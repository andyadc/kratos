package com.andyadc.kratos.container.server.handler;

import com.andyadc.kratos.common.util.RemotingUtils;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Http连接管理器
 */
public class HttpServerConnectionHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(HttpServerConnectionHandler.class);

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (logger.isDebugEnabled()) {
            final String remoteHost = RemotingUtils.parseChannelRemoteAddr(ctx.channel());
            logger.debug("channelRegistered:{}", remoteHost);
        }
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (logger.isDebugEnabled()) {
            final String remoteHost = RemotingUtils.parseChannelRemoteAddr(ctx.channel());
            logger.debug("channelUnregistered:{}", remoteHost);
        }
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (logger.isDebugEnabled()) {
            final String remoteHost = RemotingUtils.parseChannelRemoteAddr(ctx.channel());
            logger.debug("channelActive:{}", remoteHost);
        }
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (logger.isDebugEnabled()) {
            final String remoteHost = RemotingUtils.parseChannelRemoteAddr(ctx.channel());
            logger.debug("channelInactive:{}", remoteHost);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent event) {
            if (IdleState.ALL_IDLE.equals(event.state())) {
                if (logger.isDebugEnabled()) {
                    final String remoteHost = RemotingUtils.parseChannelRemoteAddr(ctx.channel());
                    logger.debug("userEventTriggered:{}", remoteHost);
                }
                ctx.channel().close();
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (logger.isDebugEnabled()) {
            final String remoteHost = RemotingUtils.parseChannelRemoteAddr(ctx.channel());
            logger.debug("exceptionCaught:{}", remoteHost);
        }
        ctx.channel().close();
    }

}
