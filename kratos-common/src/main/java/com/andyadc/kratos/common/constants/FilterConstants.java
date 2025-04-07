package com.andyadc.kratos.common.constants;

public interface FilterConstants {

    /***************负载均衡过滤器****************/
    String LOADBALANCER_FILTER_ID = "loadbalancerFilter";
    String LOADBALANCER_FILTER_NAME = "负载均衡过滤器";
    int LOADBALANCER_FILTER_ORDER = 2000;

    /***************http过滤器*****************/
    String HTTP_FILTER_ID = "httpFilter";
    String HTTP_FILTER_NAME = "HTTP过滤器";
    int HTTP_FILTER_ORDER = 2100;

    /***************rpc过滤器*****************/
    String RPC_FILTER_ID = "rpcFilter";
    String RPC_FILTER_NAME = "RPC过滤器";
    int RPC_FILTER_ORDER = 5000;

    /***************websocket过滤器*****************/
    String WEBSOCKET_FILTER_ID = "rpcFilter";
    String WEBSOCKET_FILTER_NAME = "RPC过滤器";
    int WEBSOCKET_FILTER_ORDER = 5000;

    /**************Mock过滤器*****************/
    String MOCK_FILTER_ID = "mockFilter";
    String MOCK_FILTER_NAME = "MOCK过滤器";
    int MOCK_FILTER_ORDER = 3000;

    /**************Gray过滤器*****************/
    String GRAY_FILTER_ID = "grayFilter";
    String GRAY_FILTER_NAME = "Gray过滤器";
    int GRAY_FILTER_ORDER = 2100;

    /**************timeout过滤器*****************/
    String TIMEOUT_FILTER_ID = "timeoutFilter";
    String TIMEOUT_FILTER_NAME = "timeout过滤器";
    int TIMEOUT_FILTER_ORDER = 2200;

    /**************错误处理过滤器*****************/
    String ERROR_FILTER_ID = "errorFilter";
    String ERROR_FILTER_NAME = "error过滤器";
    int ERROR_FILTER_ORDER = 20000;

    /**************统计分析过滤器*****************/
    String STATISTICS_FILTER_ID = "statisticsFilter";
    String STATISTICS_FILTER_NAME = "STATISTICS过滤器";
    int STATISTICS_FILTER_ORDER = Integer.MAX_VALUE;

}
