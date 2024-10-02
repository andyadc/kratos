package com.andyadc.kratos.context.attribute;

import com.andyadc.kratos.context.definition.ServiceInstance;
import com.andyadc.kratos.context.invoker.ServiceInvoker;

import java.util.Map;
import java.util.Set;

/**
 * 获取AttributeKey的工厂类
 */
public final class AttributeKeyFactory {

    public static AttributeKey<ServiceInvoker> getHttpInvoker() {
        return AttributeKey.HTTP_INVOKER;
    }

    public static AttributeKey<ServiceInvoker> getRpcInvoker() {
        return AttributeKey.RPC_INVOKER;
    }

    public static AttributeKey<ServiceInvoker> getWebSocketInvoker() {
        return AttributeKey.WEBSOCKET_INVOKER;
    }

    public static AttributeKey<Set<ServiceInstance>> getMatchInstances() {
        return AttributeKey.MATCH_INSTANCES;
    }

    public static AttributeKey<ServiceInstance> getLoadInstance() {
        return AttributeKey.LOAD_INSTANCE;
    }

    public static AttributeKey<Map<String, String>> getAttachment() {
        return AttributeKey.ATTACHMENT;
    }

    public static AttributeKey<?> getAttributeKey(String name) {
        return AttributeKey.getAttributeKey(name);
    }
}
