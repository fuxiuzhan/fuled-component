package com.fxz.fuled.dynamic.threadpool.wrapper;

import com.fxz.fuled.dynamic.threadpool.RpcContext;
import com.fxz.fuled.dynamic.threadpool.manage.ThreadExecuteHook;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 通过包装threadFactory实现threadLocal
 * 上下文的传递
 * 双管齐下
 * 一是包装threadFactory
 * 二是包装queue
 * 当线程池初始化时或者核心线程数小于设定值
 * 时会使用threadFactory创建线程，此过程是同步过程
 * 所以可以通过重写threadFactory实现threadLocal传递
 * <p>
 * 当核心线程满时，任务会被放到queue中
 * 此时需要对放入队列的任务进行包装即可，
 * 放入队列的动作是同步执行，由父线程执行
 *
 * @author fxz
 */
public class ThreadFactoryWrapper implements ThreadFactory {

    private ThreadFactory threadFactory;
    private ThreadExecuteHook threadExecuteHook;

    private AtomicLong counter = new AtomicLong(0);
    private String threadPoolName;

    public long getCounter() {
        return counter.get();
    }

    public ThreadFactoryWrapper(ThreadFactory threadFactory, ThreadExecuteHook threadExecuteHook, String threadPoolName) {
        this.threadFactory = threadFactory;
        this.threadExecuteHook = threadExecuteHook;
        this.threadPoolName = threadPoolName;
    }

    @Override
    public Thread newThread(Runnable r) {
        counter.incrementAndGet();
        return threadFactory.newThread(new RunnableWrapper(r, RpcContext.get(), threadExecuteHook, threadPoolName, Boolean.TRUE));
    }
}
