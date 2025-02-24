package com.andyadc.kratos.common.enums;

/**
 * 过滤类型
 */
public enum FilterType {

    PRE("PRE", "前置过滤器"),

    ROUTE("ROUTE", "中置过滤器"),

    ERROR("ERROR", "前置过滤器"),

    POST("POST", "前置过滤器"),

    MOCK("MOCK", "Mock过滤器"),

    GRAY("GRAY", "灰度过滤器");

    private final String code;

    private final String message;

    FilterType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

}
