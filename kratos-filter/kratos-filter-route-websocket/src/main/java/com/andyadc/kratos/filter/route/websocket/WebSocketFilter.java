package com.andyadc.kratos.filter.route.websocket;

import com.andyadc.kratos.common.constants.FilterConstants;
import com.andyadc.kratos.common.enums.FilterType;
import com.andyadc.kratos.context.ctx.GatewayContext;
import com.andyadc.kratos.context.rule.FilterRuleConfig;
import com.andyadc.kratos.filter.api.annotation.Filter;
import com.andyadc.kratos.filter.api.entry.AbstractEntryGatewayFilter;
import com.andyadc.kratos.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebSocket过滤器
 */
@SPIClass
@Filter(
        id = FilterConstants.WEBSOCKET_FILTER_ID,
        name = FilterConstants.WEBSOCKET_FILTER_NAME,
        value = FilterType.ROUTE,
        order = FilterConstants.WEBSOCKET_FILTER_ORDER
)
public class WebSocketFilter extends AbstractEntryGatewayFilter<FilterRuleConfig> {

    private final Logger logger = LoggerFactory.getLogger(WebSocketFilter.class);

    public WebSocketFilter() {
        super(FilterRuleConfig.class);
    }

    @Override
    public void execute(GatewayContext gatewayContext, Object... args) throws Exception {

    }

}
