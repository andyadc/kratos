package com.andyadc.kratos.processor.api.concurrent.queue.batchevent.holder;

import com.lmax.disruptor.EventTranslatorOneArg;

public class HolderEventTranslator<T> implements EventTranslatorOneArg<Holder<T>, T> {

    @Override
    public void translateTo(Holder<T> holder, long sequence, T event) {
        holder.setEvent(event);
    }

}
