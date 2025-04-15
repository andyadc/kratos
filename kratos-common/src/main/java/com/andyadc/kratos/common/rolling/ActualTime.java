package com.andyadc.kratos.common.rolling;

import com.andyadc.kratos.common.time.SystemClock;

/**
 * 实际实现
 */
public class ActualTime implements Time {

    @Override
    public long getCurrentTimeInMillis() {
        return SystemClock.millisClock().now();
    }

}
