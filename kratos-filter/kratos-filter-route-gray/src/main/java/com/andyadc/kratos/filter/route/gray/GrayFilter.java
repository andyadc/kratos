package com.andyadc.kratos.filter.route.gray;

import com.andyadc.kratos.common.constants.Constants;
import com.andyadc.kratos.common.constants.FilterConstants;
import com.andyadc.kratos.common.enums.FilterType;
import com.andyadc.kratos.context.ctx.GatewayContext;
import com.andyadc.kratos.context.ctx.RequestResponseContext;
import com.andyadc.kratos.context.rule.FilterRuleConfig;
import com.andyadc.kratos.filter.api.annotation.Filter;
import com.andyadc.kratos.filter.api.entry.AbstractEntryGatewayFilter;
import com.andyadc.kratos.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SPIClass
@Filter(
        id = FilterConstants.GRAY_FILTER_ID,
        name = FilterConstants.GRAY_FILTER_NAME,
        value = FilterType.ROUTE,
        order = FilterConstants.GRAY_FILTER_ORDER
)
public class GrayFilter extends AbstractEntryGatewayFilter<FilterRuleConfig> {

    private final Logger logger = LoggerFactory.getLogger(GrayFilter.class);

    public GrayFilter() {
        super(FilterRuleConfig.class);
    }

    @Override
    public void execute(GatewayContext context, Object... args) throws Exception {
        // 灰度过滤器
        RequestResponseContext requestResponseContext = (RequestResponseContext) context;
        String gray = requestResponseContext.getRequest().getHeaders().get(Constants.GRAY_HEADER);
        if (Constants.TRUE.equals(gray)) {
            if (logger.isDebugEnabled()) {
                logger.debug("execute gray filter");
            }
            context.setGray(true);
        }
    }

}
