package com.andyadc.kratos.common.timer;

public interface TimerTask {

    void run(Timeout timeout) throws Exception;
}
