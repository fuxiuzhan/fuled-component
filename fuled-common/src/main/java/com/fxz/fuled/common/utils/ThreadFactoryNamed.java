package com.fxz.fuled.common.utils;

import lombok.Builder;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author fxz
 */
@Builder
public class ThreadFactoryNamed implements ThreadFactory {
    private String namePrefix;
    private boolean daemon = Boolean.TRUE;
    private AtomicLong counter = new AtomicLong(0);

    ThreadFactoryNamed(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    ThreadFactoryNamed(String namePrefix, boolean daemon) {
        this.namePrefix = namePrefix;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setDaemon(daemon);
        thread.setName(namePrefix + "-" + counter.incrementAndGet());
        return thread;
    }
}
