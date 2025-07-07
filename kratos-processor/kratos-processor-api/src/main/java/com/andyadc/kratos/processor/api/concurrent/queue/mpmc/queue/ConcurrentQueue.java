package com.andyadc.kratos.processor.api.concurrent.queue.mpmc.queue;

import java.util.List;

/**
 * 并发Queue，参考java.util.Queue
 */
public interface ConcurrentQueue<E> {

    boolean offer(E e);

    E poll();

    E peek();

    long size();

    long capacity();

    boolean isEmpty();

    boolean contains(Object o);

    int remove(E[] e);

    int remove(List<E> e);

    void clear();

}
