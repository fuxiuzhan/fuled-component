package com.fxz.fuled.threadpool.monitor.wrapper;

import java.util.concurrent.*;

/**
 * @author fxz
 */
public class ThreadPoolExecutorWrapper extends ThreadPoolExecutor {
    public ThreadPoolExecutorWrapper(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }
}
