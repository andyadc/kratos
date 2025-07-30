package com.andyadc.kratos.loadbalancer.context;

import com.andyadc.kratos.context.definition.ServiceInstance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 连接数上下文
 */
public class ConnectionsContext {

    private static final Map<String, AtomicLong> CONNECTIONS_MAP = new ConcurrentHashMap<>();

    public static void add(ServiceInstance serviceInstance) {
        CONNECTIONS_MAP.computeIfAbsent(generateKey(serviceInstance), t -> new AtomicLong(0L)).incrementAndGet();
    }

    public static long getValue(ServiceInstance serviceInstance) {
        return CONNECTIONS_MAP.get(generateKey(serviceInstance)).get();
    }

    private static String generateKey(ServiceInstance serviceInstance) {
        return serviceInstance.getAddress().concat(":").concat(String.valueOf(serviceInstance.getPort()));
    }

}
