package com.andyadc.kratos.common.exception;

import com.andyadc.kratos.common.enums.ResponseCode;

import java.io.Serial;

public class NullException extends BaseException {

    @Serial
    private static final long serialVersionUID = -3109244266774956441L;

    public NullException(ResponseCode code) {
        super(code.message(), code);
    }

    public NullException(ResponseCode code, Throwable cause) {
        super(code.message(), cause, code);
    }

}
