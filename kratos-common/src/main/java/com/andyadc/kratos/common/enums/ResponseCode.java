package com.andyadc.kratos.common.enums;

import io.netty.handler.codec.http.HttpResponseStatus;

public enum ResponseCode {

    SUCCESS(HttpResponseStatus.OK, 0, "成功"),
    INTERNAL_ERROR(HttpResponseStatus.INTERNAL_SERVER_ERROR, 1000, "网关内部错误"),
    SERVICE_UNAVAILABLE(HttpResponseStatus.SERVICE_UNAVAILABLE, 2000, "服务暂时不可用,请稍后再试"),

    REQUEST_PARSE_ERROR(HttpResponseStatus.BAD_REQUEST, 10000, "请求解析错误"),
    REQUEST_PARSE_ERROR_NO_UNIQUEID(HttpResponseStatus.BAD_REQUEST, 10001, "请求解析错误, header中必须存在[uniqueId]参数"),
    PATH_NO_MATCHED(HttpResponseStatus.NOT_FOUND, 10002, "没有找到匹配的路径, 请求快速失败"),
    SERVICE_DEFINITION_NOT_FOUND(HttpResponseStatus.NOT_FOUND, 10003, "未找到对应的服务定义"),
    SERVICE_INVOKER_NOT_FOUND(HttpResponseStatus.NOT_FOUND, 10004, "未找到对应的调用实例"),
    SERVICE_INSTANCE_NOT_FOUND(HttpResponseStatus.NOT_FOUND, 10005, "未找到对应的服务实例"),
    FILTER_CONFIG_PARSE_ERROR(HttpResponseStatus.INTERNAL_SERVER_ERROR, 10006, "过滤器配置解析异常"),

    REQUEST_TIMEOUT(HttpResponseStatus.GATEWAY_TIMEOUT, 10007, "连接下游服务超时"),

    HTTP_RESPONSE_ERROR(HttpResponseStatus.INTERNAL_SERVER_ERROR, 10030, "服务返回异常"),

    RPC_DISPATCH_CONFIG_EMPTY(HttpResponseStatus.INTERNAL_SERVER_ERROR, 10016, "路由配置不能为空"),
    RPC_PARAMETER_TYPE_EMPTY(HttpResponseStatus.BAD_REQUEST, 10017, "请求的参数类型不能为空"),
    RPC_PARAMETER_VALUE_ERROR(HttpResponseStatus.BAD_REQUEST, 10018, "请求参数解析错误"),
    RPC_METHOD_NOT_FOUNT(HttpResponseStatus.NOT_FOUND, 10021, "方法不存在"),
    RPC_CONNECT_ERROR(HttpResponseStatus.INTERNAL_SERVER_ERROR, 10022, "下游服务发生异常,请稍后再试"),
    RPC_REQUEST_ERROR(HttpResponseStatus.INTERNAL_SERVER_ERROR, 10028, "服务请求异常"),
    RPC_RESPONSE_ERROR(HttpResponseStatus.INTERNAL_SERVER_ERROR, 10029, "服务返回异常"),
    VERIFICATION_FAILED(HttpResponseStatus.BAD_REQUEST, 10030, "请求参数校验失败"),
    IP_BLACKLIST(HttpResponseStatus.FORBIDDEN, 10004, "请求IP在黑名单"),
    IP_WHITELIST(HttpResponseStatus.FORBIDDEN, 10005, "请求IP不在白名单"),

    ;

    private final HttpResponseStatus status;
    private final int code;
    private final String message;

    ResponseCode(HttpResponseStatus status, int code, String msg) {
        this.status = status;
        this.code = code;
        this.message = msg;
    }

    public HttpResponseStatus status() {
        return status;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

}
