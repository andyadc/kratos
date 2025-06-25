package com.andyadc.kratos.common.enums;

public enum BufferType {

    BATCH_EVENT("batchEvent", "batchEvent模式"),
    MPMC("mpmc", "mpmc模式");

    private final String code;
    private final String message;

    BufferType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static boolean isMpmc(String bufferType) {
        return MPMC.getCode().equals(bufferType);
    }

    public static boolean isBatchEvent(String bufferType) {
        return BATCH_EVENT.getCode().equals(bufferType);
    }

}
