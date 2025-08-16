package com.andyadc.kratos.loadbalancer.hash;

import com.andyadc.kratos.loadbalancer.api.ServiceLoadBalancer;
import com.andyadc.kratos.spi.annotation.SPIClass;

import java.util.List;

@SPIClass
public class HashServiceLoadBalancer<T> implements ServiceLoadBalancer<T> {

    @Override
    public T select(List<T> servers, int hashCode, String sourceIp) {
        if (servers == null || servers.isEmpty()) {
            return null;
        }
        int index = Math.abs(hashCode) % servers.size();
        return servers.get(index);
    }

}
