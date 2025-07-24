package com.andyadc.kratos.filter.pre.loadbalancer;

import com.andyadc.kratos.common.constants.FilterConstants;
import com.andyadc.kratos.common.constants.Protocol;
import com.andyadc.kratos.common.enums.FilterType;
import com.andyadc.kratos.common.enums.ResponseCode;
import com.andyadc.kratos.common.exception.GatewayException;
import com.andyadc.kratos.context.attribute.AttributeKeyFactory;
import com.andyadc.kratos.context.cache.config.ConfigCacheFactory;
import com.andyadc.kratos.context.ctx.GatewayContext;
import com.andyadc.kratos.context.ctx.RequestResponseContext;
import com.andyadc.kratos.context.definition.ServiceInstance;
import com.andyadc.kratos.context.request.HttpGatewayRequest;
import com.andyadc.kratos.context.rule.FilterRuleConfig;
import com.andyadc.kratos.filter.api.annotation.Filter;
import com.andyadc.kratos.filter.api.entry.AbstractEntryGatewayFilter;
import com.andyadc.kratos.spi.annotation.SPIClass;

import java.util.Optional;
import java.util.Set;

/**
 * 负载均衡过滤器
 */
@SPIClass
@Filter(
        id = FilterConstants.LOADBALANCER_FILTER_ID,
        name = FilterConstants.LOADBALANCER_FILTER_NAME,
        value = FilterType.PRE,
        order = FilterConstants.LOADBALANCER_FILTER_ORDER
)
public class LoadBalancerFilter extends AbstractEntryGatewayFilter<FilterRuleConfig> {

    public LoadBalancerFilter() {
        super(FilterRuleConfig.class);
    }

    @Override
    public void execute(GatewayContext context, Object... args) throws Exception {
        try {
            RequestResponseContext requestResponseContext = (RequestResponseContext) context;
            // 获取负载均衡类型
            String balanceType = filterConfig.getBalanceType();
            String protocol = requestResponseContext.getProtocol();
            switch (protocol) {
                case Protocol.HTTP:
                    this.executeHttpLoadBalance(requestResponseContext, balanceType);
                    break;
                case Protocol.RPC:
                    this.executeRpcLoadBalance(requestResponseContext, balanceType);
                    break;
                case Protocol.WEBSOCKET:
                    this.executeWebSocketLoadBalance(requestResponseContext, balanceType);
                    break;
                default:
                    break;
            }
        } finally {
            super.nextFilter(context, args);
        }
    }

    /**
     * 执行HTTP负载均衡
     */
    private void executeHttpLoadBalance(RequestResponseContext requestResponseContext, String balanceType) {
        HttpGatewayRequest request = requestResponseContext.getRequest();
        String uniqueId = request.getUniqueId();
        Set<ServiceInstance> serviceInstances = ConfigCacheFactory.getInstance().getServiceInstanceByUniqueId(uniqueId, requestResponseContext.isGray());
        requestResponseContext.putAttribute(AttributeKeyFactory.getMatchInstances(), serviceInstances);
        //TODO 后续通过负载均衡SPI实现负载均衡，获取服务实例，这里先随意获取一个实例
        Optional<ServiceInstance> optional = serviceInstances.stream().findAny();
        if (optional.isEmpty()) {
            requestResponseContext.terminated();
            throw new GatewayException(ResponseCode.SERVICE_INSTANCE_NOT_FOUND);
        }
        ServiceInstance serviceInstance = optional.get();
        requestResponseContext.getModifyRequest().setRequestHost(serviceInstance.getAddress());
    }

    /**
     * 执行RPC负载均衡
     */
    private void executeRpcLoadBalance(RequestResponseContext requestResponseContext, String balanceType) {
        // 设置负载均衡策略到上下文
        requestResponseContext.putAttribute(balanceType, balanceType);
    }

    /**
     * 执行负载WebSocket负载均衡
     */
    private void executeWebSocketLoadBalance(RequestResponseContext requestResponseContext, String balanceType) {
        //TODO
    }

}
