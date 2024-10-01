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
}
