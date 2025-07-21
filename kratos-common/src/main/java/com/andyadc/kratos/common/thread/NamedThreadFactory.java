package com.andyadc.kratos.common.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

    private final ThreadGroup group;
    private final String namePrefix;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private boolean daemon;

    public NamedThreadFactory(String name, boolean daemon) {
        this(name);
        this.daemon = daemon;
    }

    public NamedThreadFactory(String name) {
        group = Thread.currentThread().getThreadGroup();
        namePrefix = name + "-thread-";
        daemon = Thread.currentThread().isDaemon();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        t.setDaemon(daemon);

        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }

        return t;
    }

}
