package com.andyadc.kratos.processor.api.concurrent.queue.batchevent.holder;

/**
 * Holder
 */
public class Holder<T> {

    private T event;

    public T getEvent() {
        return event;
    }

    public void setEvent(T event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "Holder{" +
                "event=" + event +
                '}';
    }

}
