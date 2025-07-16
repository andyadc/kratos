package com.andyadc.kratos.filter.error;

import com.andyadc.kratos.common.constants.FilterConstants;
import com.andyadc.kratos.common.enums.FilterType;
import com.andyadc.kratos.common.enums.ResponseCode;
import com.andyadc.kratos.common.exception.BaseException;
import com.andyadc.kratos.context.ctx.GatewayContext;
import com.andyadc.kratos.context.factory.ResponseFactory;
import com.andyadc.kratos.context.response.GatewayResponseData;
import com.andyadc.kratos.context.rule.FilterRuleConfig;
import com.andyadc.kratos.filter.api.annotation.Filter;
import com.andyadc.kratos.filter.api.entry.AbstractEntryGatewayFilter;
import com.andyadc.kratos.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 错误处理过滤器
 */
@SPIClass
@Filter(
        id = FilterConstants.ERROR_FILTER_ID,
        name = FilterConstants.ERROR_FILTER_NAME,
        value = FilterType.ERROR,
        order = FilterConstants.ERROR_FILTER_ORDER
)
public class ErrorFilter extends AbstractEntryGatewayFilter<FilterRuleConfig> {

    private final Logger logger = LoggerFactory.getLogger(ErrorFilter.class);

    public ErrorFilter() {
        super(FilterRuleConfig.class);
    }

    @Override
    public void execute(GatewayContext context, Object... args) throws Exception {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("execute error filter");
            }
            Throwable throwable = context.getThrowable();
            ResponseCode responseCode = ResponseCode.INTERNAL_ERROR;
            if (throwable instanceof BaseException exception) {
                responseCode = exception.getResponseCode();
            }
            GatewayResponseData response = ResponseFactory.getGatewayResponseWithCode(responseCode);
            context.setResponse(response);
        } finally {
            context.written();
            super.nextFilter(context, args);
        }
    }

}
