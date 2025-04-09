package com.andyadc.kratos.filter.api.factory;

import com.andyadc.kratos.common.enums.FilterType;
import com.andyadc.kratos.common.util.CollectionUtils;
import com.andyadc.kratos.context.ctx.GatewayContext;
import com.andyadc.kratos.filter.api.GatewayFilter;
import com.andyadc.kratos.filter.api.annotation.Filter;
import com.andyadc.kratos.spi.annotation.SPIClass;
import com.andyadc.kratos.spi.loader.ExtensionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认过滤器工厂实现类，此类为单例实现
 */
public class SingletonFilterFactory extends AbstractFilterFactory {

    private final Logger logger = LoggerFactory.getLogger(SingletonFilterFactory.class);

    private SingletonFilterFactory() {
        // 通过SPI加载所有的Filter集合
        Map<String, List<GatewayFilter<GatewayContext>>> map = new LinkedHashMap<>();

        // 网关会加载所有的过滤器，所以这里可以使用JDK自带的SPI功能，如果想了解对标Dubbo的SPI实现方式，可参见RPC项目
//        ServiceLoader<GatewayFilter> serviceLoader = ServiceLoader.load(GatewayFilter.class);
//        for (GatewayFilter<GatewayContext> filter : serviceLoader){
//            Filter filterAnnotation = filter.getClass().getAnnotation(Filter.class);
//            if (filterAnnotation != null){
//                String filterType = filterAnnotation.value().code();
//                List<GatewayFilter<GatewayContext>> filterList = map.get(filterType);
//                if (filterList == null){
//                    filterList = new ArrayList<>();
//                }
//                filterList.add(filter);
//                map.put(filterType, filterList);
//            }
//        }

        // 使用自定义SPI
        List<GatewayFilter> filterInstanceList = ExtensionLoader.getExtensionLoader(GatewayFilter.class).getSpiClassInstances();
        for (GatewayFilter<GatewayContext> filter : filterInstanceList) {
            Class<? extends GatewayFilter> filterClass = filter.getClass();
            SPIClass spiClassAnnotation = filterClass.getAnnotation(SPIClass.class);
            if (spiClassAnnotation == null || !spiClassAnnotation.isLoad()) {
                continue;
            }
            Filter filterAnnotation = filterClass.getAnnotation(Filter.class);
            if (filterAnnotation != null) {
                String filterType = filterAnnotation.value().code();
                List<GatewayFilter<GatewayContext>> filterList = map.get(filterType);
                if (filterList == null) {
                    filterList = new ArrayList<>();
                }
                filterList.add(filter);
                map.put(filterType, filterList);
            }
        }

        // 循环枚举类型数据
        for (FilterType filterType : FilterType.values()) {
            List<GatewayFilter<GatewayContext>> filterList = map.get(filterType.code());
            if (CollectionUtils.isEmpty(filterList)) {
                continue;
            }
            // 排序
            filterList.sort(Comparator.comparingInt(o -> o.getClass().getAnnotation(Filter.class).order()));
            try {
                super.buildFilterChain(filterType, filterList);
            } catch (Exception e) {
                logger.error("加载网关过滤器异常: ", e);
            }
        }
    }

    public static SingletonFilterFactory getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public void executeFilterChain(GatewayContext context) throws Exception {
        try {
            defaultFilterChain.execute(context);
        } catch (Throwable t) {
            logger.error("执行过滤器链异常: ", t);
            // 将异常设置到上下文
            context.setThrowable(t);
            if (context.isTerminated()) {
                context.running();
            }
            this.executeErrorFilterChain(context);
        }
    }

    @Override
    public void executeErrorFilterChain(GatewayContext context) throws Exception {
        try {
            errorFilterChain.execute(context);
        } catch (Throwable t) {
            logger.error("执行异常过滤器链异常: ", t);
        }
    }

    @Override
    public void executeMockFilterChain(GatewayContext context) throws Exception {
        try {
            mockFilterChain.execute(context);
        } catch (Throwable t) {
            logger.error("执行过滤器链异常: ", t);
            // 将异常设置到上下文
            context.setThrowable(t);
            if (context.isTerminated()) {
                context.running();
            }
            this.executeErrorFilterChain(context);
        }
    }

    @Override
    public void executeGrayFilterChain(GatewayContext context) throws Exception {
        try {
            grayFilterChain.execute(context);
        } catch (Throwable t) {
            logger.error("执行过滤器链异常: ", t);
            // 将异常设置到上下文
            context.setThrowable(t);
            if (context.isTerminated()) {
                context.running();
            }
            this.executeErrorFilterChain(context);
        }
    }

    private static class SingletonHolder {
        private static final SingletonFilterFactory INSTANCE = new SingletonFilterFactory();
    }

}
