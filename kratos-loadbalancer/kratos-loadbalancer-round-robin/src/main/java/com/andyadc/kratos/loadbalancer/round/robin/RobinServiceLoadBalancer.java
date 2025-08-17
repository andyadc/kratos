package com.andyadc.kratos.loadbalancer.round.robin;

import com.andyadc.kratos.loadbalancer.api.ServiceLoadBalancer;
import com.andyadc.kratos.spi.annotation.SPIClass;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SPIClass
public class RobinServiceLoadBalancer<T> implements ServiceLoadBalancer<T> {

    private final AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public T select(List<T> servers, int hashCode, String sourceIp) {
        if (servers == null || servers.isEmpty()) {
            return null;
        }
        int count = servers.size();
        int index = atomicInteger.incrementAndGet();
        if (index >= Integer.MAX_VALUE - 10000) {
            atomicInteger.set(0);
        }
        return servers.get(index % count);
    }

}
