package com.andyadc.kratos.common.exception;

import com.andyadc.kratos.common.enums.ResponseCode;

import java.io.Serial;

/**
 * 不匹配路径异常
 */
public class NoMatchPathException extends BaseException {

    @Serial
    private static final long serialVersionUID = -1717962909445845575L;

    public NoMatchPathException() {
        this(ResponseCode.PATH_NO_MATCHED);
    }

    public NoMatchPathException(ResponseCode code) {
        super(code.message(), code);
    }

    public NoMatchPathException(Throwable cause, ResponseCode code) {
        super(code.message(), cause, code);
    }

}
