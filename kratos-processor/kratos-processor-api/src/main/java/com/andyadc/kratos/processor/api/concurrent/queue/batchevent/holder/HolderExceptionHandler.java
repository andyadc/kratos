package com.andyadc.kratos.processor.api.concurrent.queue.batchevent.holder;

import com.andyadc.kratos.processor.api.concurrent.queue.batchevent.listener.EventListener;
import com.lmax.disruptor.ExceptionHandler;

/**
 * HolderExceptionHandler
 *
 * @param <T>
 */
public class HolderExceptionHandler<T> implements ExceptionHandler<Holder<T>> {

    private final EventListener<T> eventListener;

    public HolderExceptionHandler(EventListener<T> eventListener) {
        this.eventListener = eventListener;
    }

    @Override
    public void handleEventException(Throwable throwable, long sequence, Holder<T> event) {
        Holder<T> holder = event;
        try {
            this.eventListener.onException(throwable, sequence, event.getEvent());
        } catch (Exception e) {
            // ignore
        } finally {
            holder.setEvent(null);
        }
    }

    @Override
    public void handleOnStartException(Throwable throwable) {
        throw new UnsupportedOperationException(throwable);
    }

    @Override
    public void handleOnShutdownException(Throwable throwable) {
        throw new UnsupportedOperationException(throwable);
    }

}
