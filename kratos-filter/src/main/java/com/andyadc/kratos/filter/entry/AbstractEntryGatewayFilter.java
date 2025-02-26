package com.andyadc.kratos.filter.entry;

import com.andyadc.kratos.common.constants.Constants;
import com.andyadc.kratos.common.util.JsonUtils;
import com.andyadc.kratos.context.ctx.GatewayContext;
import com.andyadc.kratos.context.rule.FilterRule;
import com.andyadc.kratos.filter.annotation.Filter;
import com.andyadc.kratos.filter.base.AbstractGatewayFilter;
import com.andyadc.kratos.filter.cache.CacheFactory;
import com.github.benmanes.caffeine.cache.Cache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 真正由子类继承的抽象过滤器
 *
 * @param <T>
 */
public abstract class AbstractEntryGatewayFilter<T> extends AbstractGatewayFilter<GatewayContext> {

    protected final Class<T> clazz;
    private final Logger logger = LoggerFactory.getLogger(AbstractEntryGatewayFilter.class);
    protected Filter filterAnnotation;
    protected Cache<String, T> cache;

    public AbstractEntryGatewayFilter(Class<T> clazz) {
        this.filterAnnotation = this.getClass().getAnnotation(Filter.class);
        this.clazz = clazz;
        this.cache = CacheFactory.getInstance().buildCache(CacheFactory.CONFIG_CACHE_ID);
    }

    @Override
    public boolean check(GatewayContext context) throws Exception {
        return context.getRule().containsFilterRule(filterAnnotation.id());
    }

    @Override
    public void transform(GatewayContext context, Object... args) throws Exception {
        T clazz = this.dynamicLoadCache(context, args);
        super.transform(context, clazz);
    }


    private T dynamicLoadCache(GatewayContext context, Object[] args) {
        FilterRule filterConfig = context.getRule().getFilterRule(filterAnnotation.id());

        String ruleId = context.getRule().getId();
        String cacheKey = ruleId + Constants.DOLLAR_SEPARATOR + filterAnnotation.id();

        T fcc = cache.getIfPresent(cacheKey);
        if (fcc == null) {
            if (filterConfig != null && StringUtils.isNotEmpty(filterConfig.getConfig())) {
                String configStr = filterConfig.getConfig();
                try {
                    fcc = JsonUtils.parse(configStr, clazz);
                    cache.put(cacheKey, fcc);
                } catch (Exception e) {
                    logger.error("#AbstractEntryPolarisFilter# dynamicLoadCache filterId: {}, config parse error: {}", filterAnnotation.id(), configStr, e);
                }
            }
        }
        return fcc;
    }

}
