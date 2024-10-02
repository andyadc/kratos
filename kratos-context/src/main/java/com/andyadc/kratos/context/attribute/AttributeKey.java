package com.andyadc.kratos.context.attribute;

import com.andyadc.kratos.common.constants.Constants;
import com.andyadc.kratos.context.definition.ServiceInstance;
import com.andyadc.kratos.context.invoker.ServiceInvoker;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 属性Key接口
 *
 * @param <T>
 */
public abstract class AttributeKey<T> implements GatewayKey<T> {

    /**
     * 名称与AttributeKey对象的映射关系
     */
    static final Map<String, AttributeKey<?>> NAMED_ATTRIBUTE = new HashMap<>();

    /**
     * HTTP调用
     */
    static final AttributeKey<ServiceInvoker> HTTP_INVOKER = create(ServiceInvoker.class);

    /**
     * RPC调用
     */
    static final AttributeKey<ServiceInvoker> RPC_INVOKER = create(ServiceInvoker.class);

    /**
     * WebSocket调用
     */
    static final AttributeKey<ServiceInvoker> WEBSOCKET_INVOKER = create(ServiceInvoker.class);

    /**
     * 缓存匹配到的所有服务实例列表，主要用于负载均衡
     */
    static final AttributeKey<Set<ServiceInstance>> MATCH_INSTANCES = create(Set.class);

    /**
     * 通过负载均衡后获取到的服务实例
     */
    static final AttributeKey<ServiceInstance> LOAD_INSTANCE = create(ServiceInstance.class);

    /**
     * RPC请求透传的参数
     */
    static final AttributeKey<Map<String, String>> ATTACHMENT = create(Map.class);

    static {
        NAMED_ATTRIBUTE.put(Constants.HTTP_INVOKER, HTTP_INVOKER);
        NAMED_ATTRIBUTE.put(Constants.RPC_INVOKER, RPC_INVOKER);
        NAMED_ATTRIBUTE.put(Constants.WEBSOCKET_INVOKER, WEBSOCKET_INVOKER);
        NAMED_ATTRIBUTE.put(Constants.MATCH_INSTANCES, MATCH_INSTANCES);
        NAMED_ATTRIBUTE.put(Constants.LOAD_INSTANCE, LOAD_INSTANCE);
        NAMED_ATTRIBUTE.put(Constants.ATTACHMENT, ATTACHMENT);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <T> AttributeKey<T> create(final Class<? super T> valueClass) {
        return new ContextAttributeKey(valueClass);
    }

    static AttributeKey<?> getAttributeKey(String name) {
        return NAMED_ATTRIBUTE.get(name);
    }
}
