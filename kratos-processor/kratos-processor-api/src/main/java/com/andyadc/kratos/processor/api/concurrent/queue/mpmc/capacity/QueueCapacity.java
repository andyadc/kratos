package com.andyadc.kratos.processor.api.concurrent.queue.mpmc.capacity;

import com.andyadc.kratos.common.exception.GatewayException;

/**
 * 队列容量
 */
public class QueueCapacity {

    public static final int MAX_CAPACITY = (1 << 30);

    public static int getCapacity(int capacity) {
        int result = 1;
        if (capacity >= MAX_CAPACITY) {
            result = MAX_CAPACITY;
        } else {
            while (result < capacity) {
                result <<= 1;
            }
        }
        if (isMultiple(result)) {
            return result;
        }
        throw new GatewayException("队列容量不是2的倍数...");
    }

    private static boolean isMultiple(final int num) {
        return (num & (num - 1)) == 0;
    }

}
