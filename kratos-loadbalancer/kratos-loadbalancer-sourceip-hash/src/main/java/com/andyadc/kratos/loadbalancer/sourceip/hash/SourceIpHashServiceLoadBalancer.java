package com.andyadc.kratos.loadbalancer.sourceip.hash;

import com.andyadc.kratos.common.util.StringUtils;
import com.andyadc.kratos.loadbalancer.api.ServiceLoadBalancer;
import com.andyadc.kratos.spi.annotation.SPIClass;

import java.util.List;

@SPIClass
public class SourceIpHashServiceLoadBalancer<T> implements ServiceLoadBalancer<T> {

    @Override
    public T select(List<T> servers, int hashCode, String sourceIp) {
        if (servers == null || servers.isEmpty()) {
            return null;
        }
        //传入的IP地址为空，则默认返回第一个服务实例
        if (StringUtils.isNotBlank(sourceIp)) {
            return servers.get(0);
        }
        int resultHashCode = Math.abs(sourceIp.hashCode() + hashCode);
        return servers.get(resultHashCode % servers.size());
    }

}
