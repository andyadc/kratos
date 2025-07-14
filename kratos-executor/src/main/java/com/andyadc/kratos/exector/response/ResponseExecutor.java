package com.andyadc.kratos.exector.response;

import com.andyadc.kratos.common.time.SystemClock;
import com.andyadc.kratos.context.ctx.GatewayContext;
import com.andyadc.kratos.context.factory.ResponseFactory;
import com.andyadc.kratos.context.response.GatewayResponseData;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;

/**
 * 响应帮助类
 */
public class ResponseExecutor {

    public static void sendResponse(GatewayContext context) {

        //	设置响应时间
        context.setServerSendResponseTime(SystemClock.millisClock().now());
        //	释放资源
        context.releaseRequest();

        // 向客户端写回响应
        if (context.isWritten()) {
            FullHttpResponse httpResponse = ResponseFactory.getHttpResponse(context, (GatewayResponseData) context.getResponse());
            if (!context.isKeepAlive()) {
                context.getNettyContext().writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
            } else {  //如果是长连接
                httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                context.getNettyContext().writeAndFlush(httpResponse);  // 通过Netty回写数据
            }
            context.completed();
        } else if (context.isCompleted()) {
            // 完成后执行回调函数
            context.invokeCallback();
        }
    }

}
