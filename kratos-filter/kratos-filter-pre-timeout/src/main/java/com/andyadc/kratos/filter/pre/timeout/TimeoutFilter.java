package com.andyadc.kratos.filter.pre.timeout;

import com.andyadc.kratos.common.constants.Protocol;
import com.andyadc.kratos.context.attribute.AttributeKeyFactory;
import com.andyadc.kratos.context.config.FilterConfig;
import com.andyadc.kratos.context.ctx.GatewayContext;
import com.andyadc.kratos.context.ctx.RequestResponseContext;
import com.andyadc.kratos.context.invoker.RpcServiceInvoker;
import com.andyadc.kratos.context.invoker.WebSocketServiceInvoker;
import com.andyadc.kratos.context.request.HttpGatewayRequest;
import com.andyadc.kratos.filter.api.entry.AbstractEntryGatewayFilter;

public class TimeoutFilter extends AbstractEntryGatewayFilter<FilterConfig> {

    public TimeoutFilter() {
        super(FilterConfig.class);
    }

    @Override
    public void execute(GatewayContext context, Object... args) throws Exception {
        try {
            RequestResponseContext requestResponseContext = (RequestResponseContext) context;
            String protocol = requestResponseContext.getProtocol();
            switch (protocol) {
                case Protocol.HTTP:
                    HttpGatewayRequest request = requestResponseContext.getRequest();
                    request.setRequestTimeout(filterConfig.getTimeout());
                    break;
                case Protocol.RPC:
                    RpcServiceInvoker rpcServiceInvoker = (RpcServiceInvoker) requestResponseContext.getRequiredAttribute(AttributeKeyFactory.getRpcInvoker());
                    rpcServiceInvoker.setTimeout(filterConfig.getTimeout());
                    break;
                case Protocol.WEBSOCKET:
                    WebSocketServiceInvoker webSocketServiceInvoker = (WebSocketServiceInvoker) requestResponseContext.getRequiredAttribute(AttributeKeyFactory.getWebSocketInvoker());
                    webSocketServiceInvoker.setTimeout(filterConfig.getTimeout());
                    break;
                default:
                    break;
            }
        } finally {
            super.nextFilter(context, args);
        }
    }

}
