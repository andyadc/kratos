package com.andyadc.kratos.common.exception;

import com.andyadc.kratos.common.enums.ResponseCode;

import java.io.Serial;

public class RegistryException extends BaseException {

    @Serial
    private static final long serialVersionUID = 4328298628419634215L;

    public RegistryException(ResponseCode code) {
        super(code);
    }

    public RegistryException(String message) {
        super(message);
    }

    public RegistryException(Throwable t) {
        super(t);
    }

    public RegistryException(String message, Throwable t) {
        super(message, t);
    }

}
