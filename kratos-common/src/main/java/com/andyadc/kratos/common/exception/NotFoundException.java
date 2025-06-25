package com.andyadc.kratos.common.exception;

import com.andyadc.kratos.common.enums.ResponseCode;

import java.io.Serial;

/**
 * 未找到异常
 */
public class NotFoundException extends BaseException {

    @Serial
    private static final long serialVersionUID = 2568109735746458690L;

    public NotFoundException(ResponseCode code) {
        super(code.message(), code);
    }

    public NotFoundException(Throwable cause, ResponseCode code) {
        super(code.message(), cause, code);
    }

}
