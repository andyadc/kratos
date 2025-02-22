package com.andyadc.kratos.common.exception;

import com.andyadc.kratos.common.enums.ResponseCode;

/**
 * 基础异常类
 */
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = -7117866649425540252L;

    protected ResponseCode code;

    public BaseException() {
    }

    public BaseException(String message, ResponseCode code) {
        super(message);
        this.code = code;
    }

    public BaseException(String message, Throwable cause, ResponseCode code) {
        super(message, cause);
        this.code = code;
    }

    public BaseException(ResponseCode code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public BaseException(String message, Throwable cause,
                         boolean enableSuppression, boolean writableStackTrace, ResponseCode code) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    public ResponseCode getCode() {
        return code;
    }

}
