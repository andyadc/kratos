package com.andyadc.kratos.common.enums;

public enum BufferType {

    FLUSHER("FLUSHER", "FLUSHER模式"),
    MPMC("MPMC", "MPMC模式");

    private final String code;

    private final String message;

    BufferType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static boolean isMpmc(String bufferType) {
        return MPMC.getCode().equals(bufferType);
    }

    public static boolean isFlusher(String bufferType) {
        return FLUSHER.getCode().equals(bufferType);
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
