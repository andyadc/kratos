package com.andyadc.kratos.context.factory;

import com.andyadc.kratos.context.attribute.AttributeKeyFactory;
import com.andyadc.kratos.context.ctx.RequestResponseContext;
import com.andyadc.kratos.context.invoker.RpcServiceInvoker;
import com.andyadc.kratos.context.request.DefaultRpcGatewayRequest;
import com.andyadc.kratos.context.request.RpcGatewayRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取RPC信息工厂类
 */
public class RpcFactory {

    public static RpcGatewayRequest getRpcRequest(RpcServiceInvoker rpcServiceInvoker, Object[] parameters) {
        RpcGatewayRequest rpcGatewayRequest = new DefaultRpcGatewayRequest();
        rpcGatewayRequest.setRegistryAddress(rpcServiceInvoker.getRegisterAddress());
        rpcGatewayRequest.setInterfaceClass(rpcServiceInvoker.getInterfaceClass());
        rpcGatewayRequest.setMethodName(rpcServiceInvoker.getMethodName());
        rpcGatewayRequest.setParameterTypes(rpcServiceInvoker.getParameterTypes());
        rpcGatewayRequest.setArgs(parameters);
        rpcGatewayRequest.setTimeout(rpcServiceInvoker.getTimeout());
        rpcGatewayRequest.setVersion(rpcServiceInvoker.getVersion());
        return rpcGatewayRequest;
    }

    public static Map<String, String> getRpcAttachment(RequestResponseContext requestResponseContext) {
        Map<String, String> attachment = requestResponseContext.getAttribute(AttributeKeyFactory.getAttachment());
        if (attachment == null) {
            attachment = new HashMap<>();
            requestResponseContext.putAttribute(AttributeKeyFactory.getAttachment(), attachment);
        }
        return attachment;
    }

}
