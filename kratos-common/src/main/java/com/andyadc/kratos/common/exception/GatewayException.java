package com.andyadc.kratos.common.exception;

import com.andyadc.kratos.common.enums.ResponseCode;

import java.io.Serial;

public class GatewayException extends BaseException {

    @Serial
    private static final long serialVersionUID = 79405674967721560L;

    public GatewayException(String message) {
        super(message);
    }

    public GatewayException(ResponseCode code) {
        super(code);
    }

}
