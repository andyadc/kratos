package com.andyadc.kratos.processor.api.concurrent.queue.batchevent.holder;

import com.lmax.disruptor.EventFactory;

/**
 * HolderEventFactory
 *
 * @param <T>
 */
public class HolderEventFactory<T> implements EventFactory<Holder<T>> {

    @Override
    public Holder<T> newInstance() {
        return new Holder<>();
    }

}
