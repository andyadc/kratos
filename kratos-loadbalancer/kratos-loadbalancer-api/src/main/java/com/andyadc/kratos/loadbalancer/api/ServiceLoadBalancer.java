package com.andyadc.kratos.loadbalancer.api;

import com.andyadc.kratos.common.constants.Constants;
import com.andyadc.kratos.spi.annotation.SPI;

import java.util.List;

/**
 * 负载均衡接口
 */
@SPI(Constants.BALANCE_TYPE_ROUND_RIBBON)
public interface ServiceLoadBalancer<T> {

    /**
     * 以负载均衡的方式选取一个服务节点
     *
     * @param servers  服务列表
     * @param hashCode Hash值
     * @param sourceIp 源IP地址
     * @return 可用的服务节点
     */
    T select(List<T> servers, int hashCode, String sourceIp);

}
