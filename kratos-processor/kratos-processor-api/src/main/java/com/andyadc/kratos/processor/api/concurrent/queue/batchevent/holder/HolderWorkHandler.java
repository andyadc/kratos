package com.andyadc.kratos.processor.api.concurrent.queue.batchevent.holder;

import com.andyadc.kratos.processor.api.concurrent.queue.batchevent.listener.EventListener;
import com.lmax.disruptor.WorkHandler;

/**
 * HolderWorkHandler
 *
 * @param <T>
 */
public class HolderWorkHandler<T> implements WorkHandler<Holder<T>> {

    private final EventListener<T> eventListener;

    public HolderWorkHandler(EventListener<T> eventListener) {
        this.eventListener = eventListener;
    }

    @Override
    public void onEvent(Holder<T> event) throws Exception {
        this.eventListener.onEvent(event.getEvent());
        event.setEvent(null);
    }

}
