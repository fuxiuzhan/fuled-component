package com.fxz.fuled.threadpool.monitor.wrapper;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 带计数器的rejectHandler
 *
 * @author fxz
 */
public class RejectHandlerWrapper implements RejectedExecutionHandler {

    private RejectedExecutionHandler rejectedExecutionHandler;

    private AtomicLong counter = new AtomicLong(0);

    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return rejectedExecutionHandler;
    }

    public long getCounter() {
        return counter.get();
    }

    public RejectHandlerWrapper(RejectedExecutionHandler rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        counter.incrementAndGet();
        rejectedExecutionHandler.rejectedExecution(r, executor);
    }
}
