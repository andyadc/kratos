package com.andyadc.kratos.common.exception;

import com.andyadc.kratos.common.enums.ResponseCode;

import java.io.Serial;

public class ConnectionException extends BaseException {

    @Serial
    private static final long serialVersionUID = -6320572411374374828L;

    private final String uniqueId;

    private final String requestUrl;

    public ConnectionException(String uniqueId, String requestUrl) {
        this.uniqueId = uniqueId;
        this.requestUrl = requestUrl;
    }

    public ConnectionException(Throwable cause, String uniqueId, String requestUrl, ResponseCode code) {
        super(code.message(), cause, code);
        this.uniqueId = uniqueId;
        this.requestUrl = requestUrl;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

}
