package com.andyadc.kratos.processor.api.concurrent.queue.batchevent.listener;

/**
 * EventListener
 *
 * @param <T>
 */
public interface EventListener<T> {

    void onEvent(T event) throws Exception;

    void onException(Throwable ex, long sequence, T event);

}
