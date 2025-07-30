package com.andyadc.kratos.loadbalancer.random;

import com.andyadc.kratos.loadbalancer.api.ServiceLoadBalancer;
import com.andyadc.kratos.spi.annotation.SPIClass;

import java.security.SecureRandom;
import java.util.List;

/**
 * 基于随机算法的负载均衡策略
 *
 * @param <T>
 */
@SPIClass
public class RandomServiceLoadBalancer<T> implements ServiceLoadBalancer<T> {

    @Override
    public T select(List<T> servers, int hashCode, String sourceIp) {
        if (servers == null || servers.isEmpty()) {
            return null;
        }
        SecureRandom random = new SecureRandom();
        int index = random.nextInt(servers.size());
        return servers.get(index);
    }

}
