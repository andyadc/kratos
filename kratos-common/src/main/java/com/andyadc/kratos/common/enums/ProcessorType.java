package com.andyadc.kratos.common.enums;

public enum ProcessorType {

    BATCH_EVENT_PROCESSOR("batchEventProcessor", "batchEvent模式"),
    MPMC_PROCESSOR("mpmcProcessor", "mpmc模式");

    private final String code;
    private final String message;

    ProcessorType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static boolean isBatchEventOrMpmc(String processorType) {
        return BATCH_EVENT_PROCESSOR.getCode().equals(processorType) || MPMC_PROCESSOR.getCode().equals(processorType);
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
