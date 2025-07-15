package com.andyadc.kratos.common.constants;

import java.util.regex.Pattern;

public interface Constants {

    String DEFAULT_CHARSET = "UTF-8";

    String PATH_SEPARATOR = "/";

    String PATH_PATTERN = "/**";

    String QUESTION_SEPARATOR = "?";

    String ASTERISK_SEPARATOR = "*";

    String AND_SEPARATOR = "&";

    String EQUAL_SEPARATOR = "=";

    String BLANK_SEPARATOR_1 = "";

    String BLANK_SEPARATOR_2 = " ";

    String COMMA_SEPARATOR = ",";

    String SEMICOLON_SEPARATOR = ";";

    String DOLLAR_SEPARATOR = "$";

    String PIPELINE_SEPARATOR = "|";

    String BAR_SEPARATOR = "-";

    String COLON_SEPARATOR = ":";

    String DIT_SEPARATOR = ".";

    String FORWARD_VALUE_SPLIT = ", ";

    String HTTP_PREFIX_SEPARATOR = "http://";

    String HTTPS_PREFIX_SEPARATOR = "https://";

    String HTTP_FORWARD_SEPARATOR = "X-Forwarded-For";

    Pattern PARAM_PATTERN = Pattern.compile("\\{(.*?)\\}");

    String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    String DATE_FORMAT = "yyyy-MM-dd";

    String ENABLE = "Y";

    String DISABLE = "N";

    String CODE = "code";

    String STATUS = "status";

    String DATA = "data";

    String MESSAGE = "message";

    String HTTP_INVOKER = "http_invoker";

    String RPC_INVOKER = "rpc_invoker";

    String WEBSOCKET_INVOKER = "websocket_invoker";

    String MATCH_INSTANCES = "match_instances";

    String LOAD_INSTANCE = "load_instance";

    String ATTACHMENT = "attachment";

    String UNIQUE_ID = "uniqueId";

    String RPC_TRANSFER_CONTEXT = "RPC_TRANSFER_CONTEXT";

    String APPLICATION_CONFIG_NAME = "kratos-consumer";

    String APPLICATION_OWNER = "kratos";

    String APPLICATION_ORGANIZATION = "kratos";

    int DEFAULT_TIMEOUT = 5000;

    String DEFAULT_FILTER_CHAIN = "defaultFilterChain";

    String ERROR_FILTER_CHAIN = "errorFilterChain";

    String MOCK_FILTER_CHAIN = "mockFilterChain";

    String GRAY_FILTER_CHAIN = "grayFilterChain";

    String STRATEGY_BLOCKING = "blocking";

    String STRATEGY_YIELDING = "yielding";

    String STRATEGY_SLEEPING = "sleeping";

    String STRATEGY_BUSYSPIN = "busyspin";

    String RLB_NAME = "rlb";

    String KRATOS_GATEWAY = "kratos-gateway";

    String RESPONSE_MOCK = "mock success";

    String GRAY_HEADER = "gray";

    String TRUE = "true";

    int CACHE_LINE = Integer.getInteger("Intel.CacheLineSize", 64);

    int CACHE_LINE_REFS = CACHE_LINE / Long.BYTES;

    long DEFAULT_PARK_TIMEOUT = 1_000_000L;

    String JVM_PARAMS_PREFIX = "--";

    int MIN_PORT = 0;

    int MAX_PORT = 65535;

    String BOSS_EPOLL = "BOSS_EPOLL";

    String WORK_EPOLL = "WORK_EPOLL";

    String BALANCE_TYPE_ROUND_RIBBON = "roundRibbonBalance";

}
