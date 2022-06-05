package com.fxz.fuled.threadpool.monitor.wrapper;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * @author fxz
 */
public class ScheduledThreadPoolExecutorWrapper extends ScheduledThreadPoolExecutor {
    public ScheduledThreadPoolExecutorWrapper(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, threadFactory, handler);
    }
}
