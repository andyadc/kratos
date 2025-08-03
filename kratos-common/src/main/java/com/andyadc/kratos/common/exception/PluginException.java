package com.andyadc.kratos.common.exception;

import com.andyadc.kratos.common.enums.ResponseCode;

import java.io.Serial;

/**
 * 插件异常
 */
public class PluginException extends BaseException {

    @Serial
    private static final long serialVersionUID = -7435529924956336780L;

    public PluginException(ResponseCode code) {
        super(code);
    }

    public PluginException(String message) {
        super(message);
    }

    public PluginException(Throwable t) {
        super(t);
    }

    public PluginException(String message, Throwable t) {
        super(message, t);
    }

}
