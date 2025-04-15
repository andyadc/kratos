package com.andyadc.kratos.common.rolling;

/**
 * RollingNumberEvent
 */
public enum RollingNumberEvent {

    // 成功
    SUCCESS(1, 1),
    // 失败
    FAILURE(1, 2),
    // 请求超时
    REQUEST_TIMEOUT(1, 3),
    // 路由转发超时
    ROUTE_TIMEOUT(1, 4);

    private final int type;
    private final int name;

    RollingNumberEvent(int type, int name) {
        this.type = type;
        this.name = name;
    }

    public boolean isCounter() {
        return type == 1;
    }

    public boolean isMaxUpdater() {
        return type == 2;
    }

    public int getName() {
        return name;
    }

}
