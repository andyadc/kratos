package com.andyadc.kratos.processor.api.concurrent.queue.mpmc.queue;

import com.andyadc.kratos.common.util.CollectionUtils;
import com.andyadc.kratos.processor.api.concurrent.queue.mpmc.condition.AbstractCondition;
import com.andyadc.kratos.processor.api.concurrent.queue.mpmc.condition.Condition;
import com.andyadc.kratos.processor.api.concurrent.queue.mpmc.policy.PolicyEnum;
import com.andyadc.kratos.processor.api.concurrent.queue.mpmc.thread.ThreadWait;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class MpmcBlockingQueue<E> extends MpmcConcurrentQueue<E> implements Queue<E>, BlockingQueue<E>, ConcurrentQueue<E>, Iterable<E>, Collection<E>, Serializable {

    @Serial
    private static final long serialVersionUID = -585359006081669830L;

    /**
     * 当队列满时阻塞
     */
    private final Condition notFull;
    /**
     * 当队列空时阻塞
     */
    private final Condition notEmpty;

    public MpmcBlockingQueue(final int capacity) {
        this(capacity, PolicyEnum.WAITING);
    }

    public MpmcBlockingQueue(final int capacity, Collection<? extends E> c) {
        this(capacity);
        if (!CollectionUtils.isEmpty(c)) {
            c.forEach(this::offer);
        }
    }

    public MpmcBlockingQueue(final int capacity, final PolicyEnum policyEnum) {
        super(capacity);
        switch (policyEnum) {
            case BLOCKING -> {
                notFull = new NotFullCondition();
                notEmpty = new NotEmptyCondition();
            }
            case SPINNING -> {
                notFull = new SpinNotFullCondition();
                notEmpty = new SpinNotEmptyCondition();
            }
            default -> {
                notFull = new WaitingNotFullCondition();
                notEmpty = new WaitingNotEmptyCondition();
            }
        }
    }

    @Override
    public boolean offer(E e) {
        if (super.offer(e)) {
            notEmpty.signalAll();
            return true;
        } else {
            notEmpty.signalAll();
            return false;
        }
    }

    @Override
    public E poll() {
        final E e = super.poll();
        notFull.signalAll();
        return e;
    }

    @Override
    public int remove(final E[] e) {
        final int num = super.remove(e);
        notFull.signalAll();
        return num;
    }

    @Override
    public void put(E e) throws InterruptedException {
        while (!offer(e)) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException("put method interrupted...");
            }
            notFull.await();
        }
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        for (; ; ) {
            if (offer(e)) {
                return true;
            } else {
                if (!ThreadWait.waitTimeout(timeout, unit, notFull)) {
                    return false;
                }
            }
        }
    }

    @Override
    public E take() throws InterruptedException {
        for (; ; ) {
            E obj = poll();
            if (obj != null) {
                return obj;
            }
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
            notEmpty.await();
        }
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        for (; ; ) {
            E obj = poll();
            if (obj != null) {
                return obj;
            } else {
                if (!ThreadWait.waitTimeout(timeout, unit, notEmpty)) {
                    return null;
                }
            }
        }
    }

    @Override
    public void clear() {
        super.clear();
        notFull.signalAll();
    }

    @Override
    public int remainingCapacity() {
        return size - size();
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        return drainTo(c, size());
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        if (this == c) {
            throw new IllegalArgumentException("self is not drain...");
        }
        int num = 0;
        while (!isEmpty() && maxElements > 0) {
            final E e = poll();
            if (e != null) {
                c.add(e);
                num++;
            }
        }
        return num;
    }

    @Override
    public E remove() {
        return poll();
    }

    @Override
    public E element() {
        final E value = peek();
        if (value != null) {
            return value;
        }
        throw new NoSuchElementException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object[] toArray() {
        final E[] e = (E[]) new Object[size()];
        toArray(e);
        return e;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        remove((E[]) a);
        return a;
    }

    @Override
    public boolean add(E e) {
        if (offer(e)) {
            return true;
        }
        throw new IllegalStateException("queue is full, can not add element.");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if (CollectionUtils.isEmpty(c)) {
            return false;
        }
        for (final Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (CollectionUtils.isEmpty(c)) {
            return false;
        }
        for (final E e : c) {
            if (!offer(e)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<E> iterator() {
        return new RingIterator();
    }

    private boolean isFull() {
        return tailCounter.get() - headCounter.get() == size;
    }

    /**
     * 内部迭代器
     */
    private final class RingIterator implements Iterator<E> {

        private int index = 0;
        private E obj = null;

        private RingIterator() {
        }

        @Override
        public boolean hasNext() {
            return this.index < size();
        }

        @Override
        public E next() {
            final long pos = headCounter.get();
            final int i = (int) ((pos + index++) & mask);
            this.obj = buffer[i].entry;
            return this.obj;
        }

        @Override
        public void remove() {
            MpmcBlockingQueue.this.remove(obj);
        }
    }

    private final class NotFullCondition extends AbstractCondition {
        @Override
        public boolean checkCondition() {
            return isFull();
        }
    }

    private final class NotEmptyCondition extends AbstractCondition {
        @Override
        public boolean checkCondition() {
            return isEmpty();
        }
    }

    private final class WaitingNotFullCondition extends AbstractCondition {
        @Override
        public boolean checkCondition() {
            return isFull();
        }
    }

    private final class WaitingNotEmptyCondition extends AbstractCondition {
        @Override
        public boolean checkCondition() {
            return isEmpty();
        }
    }

    private final class SpinNotFullCondition extends AbstractCondition {
        @Override
        public boolean checkCondition() {
            return isFull();
        }
    }

    private final class SpinNotEmptyCondition extends AbstractCondition {
        @Override
        public boolean checkCondition() {
            return isEmpty();
        }
    }

}
