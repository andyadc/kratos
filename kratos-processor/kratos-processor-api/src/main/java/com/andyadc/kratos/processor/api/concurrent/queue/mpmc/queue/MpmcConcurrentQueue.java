package com.andyadc.kratos.processor.api.concurrent.queue.mpmc.queue;

import com.andyadc.kratos.processor.api.concurrent.queue.mpmc.atomic.CustomAtomicLong;

import java.util.Arrays;
import java.util.List;

/**
 * Mpmc并发环形队列实现，参考JDK中的java.util.concurrent.ConcurrentLinkedQueue类
 */
public class MpmcConcurrentQueue<E> implements ConcurrentQueue<E> {

    // 队列中元素的个数
    protected final int size;
    // 标记
    protected final long mask;
    // 队列头部计数器
    protected final CustomAtomicLong headCounter = new CustomAtomicLong(0L);
    // 队列尾部计数器
    protected final CustomAtomicLong tailCounter = new CustomAtomicLong(0L);
    // 缓冲
    protected Node<E>[] buffer;

    public MpmcConcurrentQueue(final int capacity) {
        int realCapacity = 2;
        // 这里一定要是2的倍数
        while (realCapacity < capacity) {
            realCapacity <<= 1;
        }
        this.size = realCapacity;
        this.mask = size - 1L;
        this.buffer = new Node[size];
        // 关键的一步，缓存预加载，会提升不少性能
        for (int i = 0; i < size; i++) {
            this.buffer[i] = new Node<>(i);
        }
    }

    @Override
    public boolean offer(E e) {
        Node<E> node;
        long tail = this.tailCounter.get();
        for (; ; ) {
            // 获取当前的元素
            node = buffer[(int) (tail & mask)];
            // 获取当前元素的序号
            final long seq = node.seq.get();
            // 获取差值
            final long difCount = seq - tail;
            if (difCount == 0) {
                if (this.tailCounter.compareAndSet(tail, tail + 1)) {
                    break;
                }
            } else if (difCount < 0) {
                return false;
            }
            // 高并发场景可能出现seq > tailCounter，此时以tailCounter的值为准
            else {
                tail = this.tailCounter.get();
            }
        }
        // 设置实际数据
        node.entry = e;
        //seq+1
        node.seq.set(tail + 1);
        return true;
    }

    @Override
    public E poll() {
        Node<E> node;
        long head = this.headCounter.get();
        for (; ; ) {
            node = buffer[(int) (head & mask)];
            long seq = node.seq.get();
            final long difCount = seq - (head + 1L);
            if (difCount == 0) {
                if (this.headCounter.compareAndSet(head, head + 1)) {
                    break;
                }
            } else if (difCount < 0) {
                return null;
            } else {
                //高并发场景可能出现 seq > headCounter + 1，此时重新设置node
                head = this.headCounter.get();
            }
        }
        try {
            return node.entry;
        } finally {
            node.entry = null;
            node.seq.set(head + mask + 1L);
        }
    }

    @Override
    public E peek() {
        return this.buffer[(int) (headCounter.get() & mask)].entry;
    }

    @Override
    public int size() {
        return (int) Math.max((tailCounter.get() - headCounter.get()), 0);
    }

    @Override
    public int capacity() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.headCounter.get() == this.tailCounter.get();
    }

    @Override
    public boolean contains(Object o) {
        // 遍历所有元素
        for (int i = 0; i < size(); i++) {
            // 逐步获取数组缓冲下标
            final int index = (int) ((headCounter.get() + i) & mask);
            // 当前数组缓冲下标不为空，并且与当前元素相等，则返回true，否则，继续循环
            if (buffer[index].entry != null && buffer[index].entry.equals(o)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int remove(E[] e) {
        return this.remove(Arrays.asList(e));
    }

    @Override
    public int remove(List<E> e) {
        int index = 0;
        while (index < e.size() && !isEmpty()) {
            final E entry = poll();
            if (entry != null) {
                e.add(entry);
                index++;
            }
        }
        return index;
    }

    @Override
    public void clear() {
        // 循环移除元素
        while (!isEmpty()) {
            poll();
        }
    }

    /**
     * 此内部类的主要作用就是消除伪共享对象
     * 关于伪共享对象相关的知识，可以阅读冰河出版的《深入理解高并发编程：核心原理与案例实战》一书
     */
    protected static final class Node<T> {
        // 下标计数器
        final CustomAtomicLong seq = new CustomAtomicLong(0L);
        // 存储的实际数据
        T entry;

        Node(final long value) {
            seq.set(value);
            entry = null;
        }
    }

}
