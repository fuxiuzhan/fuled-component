package com.fxz.fuled.dynamic.threadpool.wrapper;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 带计数器的rejectHandler
 * 统计被拒绝的线程数
 * 对原有的RejectHandler进行代理
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
