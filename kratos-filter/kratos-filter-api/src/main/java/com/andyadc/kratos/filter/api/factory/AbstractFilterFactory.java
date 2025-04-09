package com.andyadc.kratos.filter.api.factory;

import com.andyadc.kratos.common.constants.Constants;
import com.andyadc.kratos.common.enums.FilterType;
import com.andyadc.kratos.common.exception.GatewayException;
import com.andyadc.kratos.common.util.StringUtils;
import com.andyadc.kratos.context.ctx.GatewayContext;
import com.andyadc.kratos.filter.api.GatewayFilter;
import com.andyadc.kratos.filter.api.annotation.Filter;
import com.andyadc.kratos.filter.api.base.AbstractGatewayFilter;
import com.andyadc.kratos.filter.api.chain.DefaultFilterChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 抽象过滤器工厂
 */
public abstract class AbstractFilterFactory implements GatewayFilterFactory {

    private final Logger logger = LoggerFactory.getLogger(AbstractFilterFactory.class);
    /**
     * 过滤器类型Map
     */
    private final Map<String, Map<String, GatewayFilter<GatewayContext>>> filterTypeMap = new LinkedHashMap<>();
    /**
     * 过滤器id与过滤器的映射关系
     */
    private final Map<String, GatewayFilter<GatewayContext>> filterIdMap = new LinkedHashMap<>();
    /**
     * 正常执行的过滤器链
     */
    protected DefaultFilterChain defaultFilterChain = new DefaultFilterChain(Constants.DEFAULT_FILTER_CHAIN);
    /**
     * 错误或者异常执行的过滤器链
     */
    protected DefaultFilterChain errorFilterChain = new DefaultFilterChain(Constants.ERROR_FILTER_CHAIN);
    /**
     * Mock过滤器链
     */
    protected DefaultFilterChain mockFilterChain = new DefaultFilterChain(Constants.MOCK_FILTER_CHAIN);
    /**
     * 灰度过滤器链
     */
    protected DefaultFilterChain grayFilterChain = new DefaultFilterChain(Constants.GRAY_FILTER_CHAIN);

    @Override
    public void buildFilterChain(FilterType filterType, List<GatewayFilter<GatewayContext>> filterList) throws Exception {
        switch (filterType) {
            case PRE:
            case ROUTE:
                this.addFilter(defaultFilterChain, filterList);
                break;
            case ERROR:
                this.addFilter(errorFilterChain, filterList);
                break;
            case POST:
                this.addFilter(defaultFilterChain, filterList);
                this.addFilter(errorFilterChain, filterList);
                break;
            case MOCK:
                this.addFilter(mockFilterChain, filterList);
                break;
            case GRAY:
                this.addFilter(grayFilterChain, filterList);
                break;
            default:
                throw new GatewayException("不支持的过滤器类型...");
        }
    }


    @Override
    public <T> T getFilter(Class<T> clazz) throws Exception {
        Filter filter = clazz.getAnnotation(Filter.class);
        if (filter != null) {
            String filterId = filter.id();
            if (StringUtils.isEmpty(filterId)) {
                filterId = clazz.getName();
            }
            return this.getFilter(filterId);
        }
        return null;
    }

    @Override
    public <T> T getFilter(String filterId) throws Exception {
        GatewayFilter<GatewayContext> filter = filterIdMap.get(filterId);
        return filter == null ? null : (T) filter;
    }

    /**
     * 将过滤器添加到过滤器链
     */
    private void addFilter(DefaultFilterChain filterChain, List<GatewayFilter<GatewayContext>> filterList) throws Exception {
        if (filterList == null || filterList.isEmpty()) {
            return;
        }
        for (GatewayFilter<GatewayContext> filter : filterList) {
            filter.initFilter();
            this.builder(filterChain, filter);
        }
    }

    /**
     * 构造过滤器链
     */
    private void builder(DefaultFilterChain filterChain, GatewayFilter<GatewayContext> filter) {
        logger.info("过滤器链id:{}, 过滤器链:{}", filterChain.getId(), filter.getClass().getName());
        // 获取注解
        Filter filterAnnotation = filter.getClass().getAnnotation(Filter.class);
        if (filterAnnotation != null) {
            filterChain.addLast((AbstractGatewayFilter<GatewayContext>) filter);

            String filterId = filterAnnotation.id();
            if (StringUtils.isEmpty(filterId)) {
                filterId = filter.getClass().getName();
            }
            String code = filterAnnotation.value().code();
            Map<String, GatewayFilter<GatewayContext>> map = filterTypeMap.get(code);
            if (map == null) {
                map = new LinkedHashMap<>();
            }
            map.put(filterId, filter);
            filterTypeMap.put(code, map);
            filterIdMap.put(filterId, filter);
        }
    }

}
