package com.andyadc.kratos.common.constants;

/**
 * 协议相关常量
 */
public interface Protocol {

    String HTTP = "http";
    String RPC = "rpc";
    String WEBSOCKET = "websocket";

    static boolean isHttp(String protocol) {
        return HTTP.equalsIgnoreCase(protocol);
    }

    static boolean isRpc(String protocol) {
        return RPC.equalsIgnoreCase(protocol);
    }

    static boolean isWebSocket(String protocol) {
        return WEBSOCKET.equalsIgnoreCase(protocol);
    }

}
