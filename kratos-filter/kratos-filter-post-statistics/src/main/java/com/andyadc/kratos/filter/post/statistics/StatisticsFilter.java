package com.andyadc.kratos.filter.post.statistics;

import com.andyadc.kratos.common.constants.Constants;
import com.andyadc.kratos.common.constants.FilterConstants;
import com.andyadc.kratos.common.enums.FilterType;
import com.andyadc.kratos.common.rolling.RollingNumber;
import com.andyadc.kratos.common.rolling.RollingNumberEvent;
import com.andyadc.kratos.context.ctx.GatewayContext;
import com.andyadc.kratos.context.loader.GatewayConfigLoader;
import com.andyadc.kratos.context.rule.FilterRuleConfig;
import com.andyadc.kratos.filter.api.annotation.Filter;
import com.andyadc.kratos.filter.api.entry.AbstractEntryGatewayFilter;
import com.andyadc.kratos.spi.annotation.SPIClass;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 统计分析过滤器
 */
@SPIClass
@Filter(
        id = FilterConstants.STATISTICS_FILTER_ID,
        name = FilterConstants.STATISTICS_FILTER_NAME,
        value = FilterType.POST,
        order = FilterConstants.STATISTICS_FILTER_ORDER
)
public class StatisticsFilter extends AbstractEntryGatewayFilter<FilterRuleConfig> {

    /**
     * 统计窗口大小，默认1分钟
     */
    public static final long WINDOW_SIZE = 60 * 1000;
    /**
     * 桶大小，默认60
     */
    public static final int BUCKET_SIZE = 60;

    /**
     * RollingNumber
     */
    private final RollingNumber rollingNumber;

    /**
     * StatisticsConsumer对象
     */
    private final StatisticsConsumer statisticsConsumer = new StatisticsConsumer();

    /**
     * 统计线程，也就是指标上报的线程
     */
    private final ThreadPoolExecutor statisticsThreadPool = new ThreadPoolExecutor(1, 1, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(128), new ThreadPoolExecutor.CallerRunsPolicy());

    public StatisticsFilter() {
        super(FilterRuleConfig.class);
        this.rollingNumber = new RollingNumber(WINDOW_SIZE, BUCKET_SIZE, Constants.KRATOS_GATEWAY, statisticsConsumer.getStatisticsQueue());
    }

    @Override
    public void execute(GatewayContext context, Object... args) throws Exception {
        try {
            if (filterConfig.isRollingNumber()) {
                statisticsThreadPool.execute(statisticsConsumer);
                this.doStatisticsNumber(context, args);
            }
        } finally {
            context.terminated();
            super.nextFilter(context, args);
        }
    }

    /**
     * 执行统计数据
     */
    private void doStatisticsNumber(GatewayContext context, Object... args) {
        Throwable throwable = context.getThrowable();
        if (throwable == null) {
            // 统计成功事件
            rollingNumber.increment(RollingNumberEvent.SUCCESS);
        } else {
            // 统计失败事件
            rollingNumber.increment(RollingNumberEvent.FAILURE);
        }
        // 客户端请求开始的时间
        long serverReceiveRequestTime = context.getServerReceiveRequestTime();
        // 路由的开始时间
        long clientSendRequestTime = context.getClientSendRequestTime();
        // 路由接收时间
        long clientReceiveResponseTime = context.getClientReceiveResponseTime();
        // 请求结束的时间
        long serverSendResponseTime = context.getServerSendResponseTime();
        // 整体耗时
        long requestTimeInterval = serverSendResponseTime - serverReceiveRequestTime;
        // 获取默认的超时时间
        long defaultRequestTimeout = GatewayConfigLoader.getGatewayConfig().getRequestTimeout();
        // 请求已超时
        if (requestTimeInterval > defaultRequestTimeout) {
            rollingNumber.increment(RollingNumberEvent.REQUEST_TIMEOUT);
        }

        // 计算路由间隔时间
        long routeTimeInterval = clientReceiveResponseTime - clientSendRequestTime;
        long defaultRouteTimeout = GatewayConfigLoader.getGatewayConfig().getRouteTimeout();
        if (routeTimeInterval > defaultRouteTimeout) {
            rollingNumber.increment(RollingNumberEvent.ROUTE_TIMEOUT);
        }
    }

}
