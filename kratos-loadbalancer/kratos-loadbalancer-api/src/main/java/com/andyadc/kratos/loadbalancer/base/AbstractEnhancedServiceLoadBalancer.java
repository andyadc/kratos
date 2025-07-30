package com.andyadc.kratos.loadbalancer.base;

import com.andyadc.kratos.context.definition.ServiceInstance;
import com.andyadc.kratos.loadbalancer.api.ServiceLoadBalancer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 基础的增强型负载均衡类
 */
public abstract class AbstractEnhancedServiceLoadBalancer implements ServiceLoadBalancer<ServiceInstance> {

    /**
     * 根据权重重新生成服务实例列表，权重越高的服务实例，会在最终的列表中出现的次数越多
     * 例如，权重为1，最终出现1次，权重为2，最终出现2次，权重为3，最终出现3次，依此类推...
     */
    protected List<ServiceInstance> getWeightServiceMetaList(List<ServiceInstance> servers) {
        if (servers == null || servers.isEmpty()) {
            return null;
        }
        List<ServiceInstance> serviceMetaList = new ArrayList<>();
        servers.forEach((server) -> {
            IntStream.range(0, server.getWeight()).forEach((i) -> {
                serviceMetaList.add(server);
            });
        });
        return serviceMetaList;
    }

}
