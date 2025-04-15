package com.andyadc.kratos.common.rolling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * 桶循环数组
 */
public class BucketCircularArray implements Iterable<Bucket> {

    private final AtomicReference<ListState> state;
    private final int dataLength;
    private final long numBuckets;

    BucketCircularArray(int size) {
        AtomicReferenceArray<Bucket> _buckets = new AtomicReferenceArray<>(size + 1);
        state = new AtomicReference<>(new ListState(_buckets, 0, 0));
        dataLength = _buckets.length();
        numBuckets = size;
    }

    public void clear() {
        while (true) {
            ListState current = state.get();
            ListState newState = current.clear();
            if (state.compareAndSet(current, newState)) {
                return;
            }
        }
    }

    public Iterator<Bucket> iterator() {
        return Collections.unmodifiableList(Arrays.asList(getArray())).iterator();
    }

    public void addLast(Bucket o) {
        ListState currentState = state.get();
        ListState newState = currentState.addBucket(o);
        if (state.compareAndSet(currentState, newState)) {
        } else {
        }
    }

    public Bucket getLast() {
        return peekLast();
    }

    public int size() {
        // the size can also be worked out each time as:
        // return (tail + data.length() - head) % data.length();
        return state.get().size;
    }

    public Bucket peekLast() {
        return state.get().tail();
    }

    public Bucket[] getArray() {
        return state.get().getArray();
    }

    private class ListState {
        /**
         * 桶
         */
        private final AtomicReferenceArray<Bucket> data;

        /**
         * 长度
         */
        private final int size;

        /**
         * 头结点索引
         */
        private final int head;

        /**
         * 尾节点索引
         */
        private final int tail;

        private ListState(AtomicReferenceArray<Bucket> data, int head, int tail) {
            this.head = head;
            this.tail = tail;
            if (head == 0 && tail == 0) {
                size = 0;
            } else {
                this.size = (tail + dataLength - head) % dataLength;
            }
            this.data = data;
        }

        public Bucket tail() {
            if (size == 0) {
                return null;
            } else {
                return data.get(convert(size - 1));
            }
        }

        private Bucket[] getArray() {
            ArrayList<Bucket> array = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                array.add(data.get(convert(i)));
            }
            return array.toArray(new Bucket[0]);
        }

        private ListState incrementTail() {
            if (size == numBuckets) {
                return new ListState(data, (head + 1) % dataLength, (tail + 1) % dataLength);
            } else {
                return new ListState(data, head, (tail + 1) % dataLength);
            }
        }

        public ListState clear() {
            return new ListState(new AtomicReferenceArray<>(dataLength), 0, 0);
        }

        public ListState addBucket(Bucket b) {
            data.set(tail, b);
            return incrementTail();
        }

        // The convert() method takes a logical index (as if head was
        // always 0) and calculates the index within elementData
        private int convert(int index) {
            return (index + head) % dataLength;
        }
    }

}
