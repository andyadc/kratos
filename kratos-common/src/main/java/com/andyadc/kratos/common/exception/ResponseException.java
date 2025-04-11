package com.andyadc.kratos.common.exception;

import com.andyadc.kratos.common.enums.ResponseCode;

import java.io.Serial;

public class ResponseException extends BaseException {

    @Serial
    private static final long serialVersionUID = -6376414905402711408L;

    public ResponseException(ResponseCode code) {
        super(code);
    }

    public ResponseException(String message) {
        super(message);
    }

}
