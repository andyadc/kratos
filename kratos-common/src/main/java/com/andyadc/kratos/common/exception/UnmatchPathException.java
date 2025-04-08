package com.andyadc.kratos.common.exception;

import com.andyadc.kratos.common.enums.ResponseCode;

import java.io.Serial;

/**
 * 不匹配路径异常
 */
public class UnmatchPathException extends BaseException {

    @Serial
    private static final long serialVersionUID = 4271699809022718782L;

    public UnmatchPathException() {
        this(ResponseCode.PATH_NO_MATCHED);
    }

    public UnmatchPathException(ResponseCode code) {
        super(code.message(), code);
    }

    public UnmatchPathException(ResponseCode code, Throwable cause) {
        super(code.message(), cause, code);
    }

}
