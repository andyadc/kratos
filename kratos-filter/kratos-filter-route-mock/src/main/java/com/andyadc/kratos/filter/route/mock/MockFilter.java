package com.andyadc.kratos.filter.route.mock;


import com.andyadc.kratos.common.constants.Constants;
import com.andyadc.kratos.common.constants.FilterConstants;
import com.andyadc.kratos.common.enums.FilterType;
import com.andyadc.kratos.context.ctx.GatewayContext;
import com.andyadc.kratos.context.ctx.RequestResponseContext;
import com.andyadc.kratos.context.factory.ResponseFactory;
import com.andyadc.kratos.context.rule.FilterRule;
import com.andyadc.kratos.context.rule.FilterRuleConfig;
import com.andyadc.kratos.exector.response.ResponseExecutor;
import com.andyadc.kratos.filter.api.annotation.Filter;
import com.andyadc.kratos.filter.api.entry.AbstractEntryGatewayFilter;
import com.andyadc.kratos.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SPIClass
@Filter(
        id = FilterConstants.MOCK_FILTER_ID,
        name = FilterConstants.MOCK_FILTER_NAME,
        value = FilterType.ROUTE,
        order = FilterConstants.MOCK_FILTER_ORDER
)
public class MockFilter extends AbstractEntryGatewayFilter<FilterRuleConfig> {

    private final Logger logger = LoggerFactory.getLogger(MockFilter.class);

    public MockFilter() {
        super(FilterRuleConfig.class);
    }

    @Override
    public void execute(GatewayContext context, Object... args) throws Exception {
        RequestResponseContext requestResponseContext = (RequestResponseContext) context;
        FilterRule filterRule = requestResponseContext.getRule().getFilterRule(FilterConstants.MOCK_FILTER_ID);
        if (filterRule == null) {
            return;
        }
        requestResponseContext.setResponse(ResponseFactory.getGatewayResponse(Constants.RESPONSE_MOCK));
        requestResponseContext.written();
        ResponseExecutor.sendResponse(requestResponseContext);
        if (logger.isDebugEnabled()) {
            logger.debug(" mock {} {} {}", requestResponseContext.getRequest().getMethod(), requestResponseContext.getRequest().getPath(), Constants.RESPONSE_MOCK);
        }
        requestResponseContext.terminated();
    }

}
