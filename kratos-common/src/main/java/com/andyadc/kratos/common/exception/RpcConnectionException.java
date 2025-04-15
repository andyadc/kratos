package com.andyadc.kratos.common.exception;

import com.andyadc.kratos.common.enums.ResponseCode;

import java.io.Serial;

public class RpcConnectionException extends ConnectionException {

    @Serial
    private static final long serialVersionUID = -2685188933541738354L;

    private final String interfaceName;
    private final String methodName;

    public RpcConnectionException(String uniqueId, String requestUrl, String interfaceName, String methodName) {
        super(uniqueId, requestUrl);
        this.interfaceName = interfaceName;
        this.methodName = methodName;
    }

    public RpcConnectionException(Throwable cause, String uniqueId, String requestUrl,
                                  String interfaceName, String methodName, ResponseCode code) {
        super(cause, uniqueId, requestUrl, code);
        this.interfaceName = interfaceName;
        this.methodName = methodName;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

}
